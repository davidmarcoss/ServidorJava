package info.infomila.billar.server;

import info.infomila.billar.ipersistence.BillarException;
import info.infomila.billar.ipersistence.IBillar;
import info.infomila.billar.models.Classificacio;
import info.infomila.billar.models.Grup;
import info.infomila.billar.models.Partida;
import info.infomila.billar.models.Soci;
import info.infomila.billar.models.Torneig;
import static info.infomila.billar.server.Main.usersMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadHandler extends Thread
{

    private Socket socket;
    private IBillar billar;
    private String sessionID;

    public ThreadHandler(Socket sock, IBillar billar)
    {
        setSocket(sock);
        setBillar(billar);
    }

    public Socket getSocket()
    {
        return socket;
    }

    public void setSocket(Socket sockClient)
    {
        this.socket = sockClient;
    }

    public IBillar getBillar()
    {
        return billar;
    }

    public void setBillar(IBillar obj)
    {
        this.billar = obj;
    }

    @Override
    public void run()
    {
        ObjectInputStream dataEntrada = null;
        ObjectOutputStream dataSalida = null;

        try {
            dataSalida = new ObjectOutputStream(socket.getOutputStream());
            dataEntrada = new ObjectInputStream(socket.getInputStream());

            int opcio = dataEntrada.readInt();
            System.out.println("Opcio usuari: " + opcio);
            sessionID = "";
            switch (opcio) {
                case 1: { // LOGIN
                    String nif = (String) dataEntrada.readObject();
                    String pass = (String) dataEntrada.readObject();

                    Soci soci = billar.login(nif, pass);
                    System.out.println("LOGIN: " + nif + " - " + pass);
                    if (soci == null) {
                        dataSalida.writeInt(-1);
                    } else {
                        billar.refresh(soci);
                        sessionID = generateSessionID(soci.getNif());
                        usersMap.put(sessionID, soci);

                        dataSalida.writeInt(1);

                        dataSalida.writeObject(sessionID);
                        dataSalida.flush();

                        dataSalida.writeObject(new Soci(soci));
                        dataSalida.flush();
                    }
                    break;
                }
                case 2: { // LLISTA DE TORNEJOS OBERTS
                    sessionID = (String) dataEntrada.readObject();
                    System.out.println("LLISTA DE TORNEJOS OBERTS PER A: " + sessionID);
                    if (usersMap.containsKey(sessionID)) {
                        List<Torneig> tornejos = new ArrayList<Torneig>();
                        billar.getTornejosObertsInscripcio(usersMap.get(sessionID)).forEach((torneig) -> {
                            tornejos.add(new Torneig(torneig));
                        });
                        
                        dataSalida.writeInt(1);
                        dataSalida.flush();
                        dataSalida.writeObject(tornejos);
                        dataSalida.flush();
                    } else {
                        dataSalida.writeInt(-1);
                    }
                    break;
                }
                case 3: { // LLISTA TORNEJOS ON PARTICIPO
                    sessionID = (String) dataEntrada.readObject();
                    System.out.println("LLISTA DE TORNEJOS INSCRITS DE: " + sessionID);
                    if (usersMap.containsKey(sessionID)) {
                        List<Torneig> tornejos = new ArrayList<Torneig>();
                        billar.getTornejosActiusOnParticipo(usersMap.get(sessionID).getId()).forEach((torneig) -> {
                            tornejos.add(new Torneig(torneig));
                        });
                        
                        dataSalida.writeInt(1);
                        dataSalida.flush();
                        dataSalida.writeObject(tornejos);
                        dataSalida.flush();
                    } else {
                        System.out.println("El soci amb aquest session_id no existeix" + sessionID);
                    }
                    break;
                }
                case 4: { // FER INSCRIPCIO
                    sessionID = (String) dataEntrada.readObject();
                    System.out.println("FENT INSCRIPCIÓ DE: " + sessionID);
                    int torneigId = dataEntrada.readInt();
                    Torneig torneig = billar.getTorneigById(torneigId);
                    if (usersMap.containsKey(sessionID)) {
                        Soci sociTrobat = usersMap.get(sessionID);
                        try {
                            billar.ferInscripcio(sociTrobat, torneig);
                            billar.commit();
                            dataSalida.writeInt(1);
                            dataSalida.flush();
                        } catch (BillarException ex) {
                            try {
                                billar.rollback();
                                dataSalida.writeInt(-1);
                            } catch (BillarException ex2) {
                                dataSalida.writeInt(-1);
                            }
                        }
                    } else {
                        System.out.println("El soci amb aquest session_id no existeix" + sessionID);
                        dataSalida.writeInt(-1);
                        dataSalida.flush();
                    }
                    break;
                }
                case 5: { // UPDATE SOCI
                    sessionID = (String) dataEntrada.readObject();
                    if (usersMap.containsKey(sessionID)) {
                        usersMap.get(sessionID).setNif((String) dataEntrada.readObject());
                        usersMap.get(sessionID).setNom((String) dataEntrada.readObject());
                        usersMap.get(sessionID).setCognom1((String) dataEntrada.readObject());
                        usersMap.get(sessionID).setCognom2((String) dataEntrada.readObject());
                        usersMap.get(sessionID).setPasswordHash((String) dataEntrada.readObject());

                        billar.updateSoci(usersMap.get(sessionID));
                        billar.commit();

                        dataSalida.writeInt(1);
                        dataSalida.flush();

                    } else {
                        dataSalida.writeInt(-1);
                    }
                    break;
                }
                case 6: { // GET PARTIDES
                    sessionID = (String) dataEntrada.readObject();
                    int torneigId = dataEntrada.readInt();
                    if (usersMap.containsKey(sessionID)) {
                        Grup grup = billar.getGrupByTorneigOfSoci(torneigId, usersMap.get(sessionID).getId());
                        
                        List<Partida> partides = billar.getPartides(grup.getId(), torneigId, usersMap.get(sessionID).getId());
                        partides.forEach((partida) -> {
                            if (partida.getSociA().getNom() == null) {
                                partida.setSociA(new Soci(usersMap.get(sessionID)));
                            } else if (partida.getSociB().getNom() == null) {
                                partida.setSociB(new Soci(usersMap.get(sessionID)));
                            }
                        });
                        
                        dataSalida.writeInt(1);
                        dataSalida.flush();
                        dataSalida.writeObject(partides);
                        dataSalida.flush();
                    } else {
                        dataSalida.writeInt(-1);
                    }
                    break;
                }
                case 7: { // MODIFICAR PARTIDA
                    sessionID = (String) dataEntrada.readObject();
                    Partida partida = (Partida) dataEntrada.readObject();
                    if (usersMap.containsKey(sessionID)) {
                        try {
                            // Modifiquem i guardem la partida
                            Partida partidaUpdated = billar.getPartidaById(partida.getId());
                            partidaUpdated.setCarambolesA(partida.getCarambolesA());
                            partidaUpdated.setNumEntradesA(partida.getNumEntradesA());
                            partidaUpdated.setCarambolesB(partida.getCarambolesB());
                            partidaUpdated.setNumEntradesB(partida.getNumEntradesB());
                            partidaUpdated.setEstatPartida(partida.getEstatPartida());
                            partidaUpdated.setGuanyador(partida.getGuanyador());
                            partidaUpdated.setModeVictoria(partida.getModeVictoria());
                            billar.updatePartida(partidaUpdated);
                            billar.commit();
                            
                            // TODO: Modifiquem i guardem les estadistiques dels 2 socis de la partida
                            
                            
                            dataSalida.writeInt(1);
                            dataSalida.flush();
                        } catch (BillarException ex) {
                            dataSalida.writeInt(-1);
                            dataSalida.flush();
                        }
                    } else {
                        dataSalida.writeInt(-1);
                        dataSalida.flush();
                    }
                }
                case 8: { // GET CLASSIFICACIO
                    sessionID = (String) dataEntrada.readObject();
                    int torneigId = dataEntrada.readInt();
                    int modalitatId = dataEntrada.readInt();
                    if (usersMap.containsKey(sessionID)) {
                        Grup grup = billar.getGrupByTorneigOfSoci(torneigId, usersMap.get(sessionID).getId());

                        List<Classificacio> classificacions = new ArrayList<>();
                        Iterator iteClassificacions = billar.getClassificacio(grup.getId(), modalitatId);
                        while (iteClassificacions.hasNext()) {
                            Object[] fila = (Object[]) iteClassificacions.next();
                            int pos = (int) fila[0];
                            String nomSoci = (String) fila[1];
                            String cognom1Soci = (String) fila[2];
                            String cognom2Soci = (String) fila[3];
                            String nomComplet = nomSoci;
                            if (cognom1Soci != null) {
                                nomComplet += " " + cognom1Soci;
                            }
                            if (cognom2Soci != null) {
                                nomComplet += " " + cognom2Soci;
                            }
                            long partidesGuanyades = (long) fila[6];
                            long partidesPerdudes = (long) fila[7];
                            Classificacio classificacio = new Classificacio(nomComplet, partidesGuanyades, partidesPerdudes);
                            classificacions.add(classificacio);
                        }

                        dataSalida.writeInt(1);
                        dataSalida.flush();
                        dataSalida.writeObject(classificacions);
                        dataSalida.flush();
                    } else {
                        dataSalida.writeInt(-1);
                    }
                    break;
                }
                case 9: { // GET ESTADISTIQUES
                    sessionID = (String) dataEntrada.readObject();
                    System.out.println("GET ESTADISTIQUES DE " + sessionID);
                    if (usersMap.containsKey(sessionID)) {
                        Soci soci = billar.getEstadistiques(usersMap.get(sessionID).getNif(), usersMap.get(sessionID).getPasswordHash());
                        if (soci == null) {
                            dataSalida.writeInt(-1);
                            dataSalida.flush();
                        } else {
                            //billar.refresh(soci);
                            
                            dataSalida.writeInt(1);
                            dataSalida.flush();

                            dataSalida.writeObject(new Soci(soci));
                            dataSalida.flush();
                        }
                    } else {
                        dataSalida.writeInt(-1);
                        dataSalida.flush();
                    }
                    break;
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ThreadHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ThreadHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BillarException ex) {
            Logger.getLogger(ThreadHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getSociSessionID(Soci soci)
    {
        for (Entry<String, Soci> entry : usersMap.entrySet()) {
            if (soci.equals(entry.getValue())) {
                return entry.getKey();
            }
        }

        return null;
    }

    private String generateSessionID(String nif)
    {
        String hash = "";

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(nif.getBytes());
            byte byteData[] = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            hash = sb.toString();

        } catch (NoSuchAlgorithmException ex) {

        }

        return hash;
    }
}

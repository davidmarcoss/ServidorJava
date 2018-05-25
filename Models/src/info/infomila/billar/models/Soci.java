package info.infomila.billar.models;

import java.io.Serializable;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

@Entity
@Access(AccessType.FIELD)
@Table(name = "socis")
public class Soci implements Serializable
{
    @Transient
    private static final long serialVersionUID = 1L;
    
    @Id
    @TableGenerator(name = "gen_soci",
            table = "comptadors",
            pkColumnName = "clau",
            pkColumnValue = "socis",
            valueColumnName = "next_val",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "gen_soci")
    private int id;

    @Basic(optional = false)
    @Column(nullable = false, length = 13, unique = true)
    private String nif;

    @Basic(optional = false)
    @Column(nullable = false, length = 50)
    private String nom;

    @Column(length = 50)
    private String cognom1;

    @Column(length = 50)
    private String cognom2;

    @Basic(optional = false)
    @Column(name = "data_alta", nullable = false, length = 50)
    private Date dataAlta;

    @Basic(optional = false)
    @Column(name = "password_hash", nullable = false, length = 32)
    private String passwordHash;

    @Column
    @Lob
    private Blob foto;

    @OneToMany(mappedBy = "emPK.soci", fetch = FetchType.EAGER)
    private List<EstadisticaModalitat> estadistiques = new ArrayList<>();

    @Column()
    private boolean actiu;

    @Transient
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    protected Soci()
    {
    }

    public Soci(String nif, String nom, String cognom1, String cognom2, String passwordHash, Blob foto, boolean actiu)
    {
        setNif(nif);
        setNom(nom);
        setCognom1(cognom1);
        setCognom2(cognom2);
        setDataAlta(new Date());
        setPasswordHash(passwordHash);
        setFoto(foto);
        setActiu(actiu);
    }

    public int getId()
    {
        return id;
    }

    protected final void setId(int id)
    {
        this.id = id;
    }

    public String getNif()
    {
        return nif;
    }

    public final void setNif(String nif)
    {
        if (!validarNIF(nif)) {
            throw new SociException("El NIF es obligatori i ha de tindre una longitud de 8 dígits i 1 caràcter.");
        }
        this.nif = nif;
    }

    public String getNom()
    {
        return nom;
    }

    public final void setNom(String nom)
    {
        if (nom == null || nom.length() < 3) {
            throw new SociException("El nom es obligatori i ha de tindre un mínim de 2 caràcters.");
        }
        this.nom = nom;
    }

    public String getCognom1()
    {
        return cognom1;
    }

    public final void setCognom1(String cognom1)
    {
        this.cognom1 = cognom1;
    }

    public String getCognom2()
    {
        return cognom2;
    }

    public final void setCognom2(String cognom2)
    {
        this.cognom2 = cognom2;
    }

    public Date getDataAlta()
    {
        return dataAlta;
    }

    protected final void setDataAlta(Date dataAlta)
    {
        this.dataAlta = dataAlta;
    }

    public String getDataAltaString()
    {
        return sdf.format(dataAlta);
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    public final void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }

    public Blob getFoto()
    {
        return foto;
    }

    public final void setFoto(Blob foto)
    {
        this.foto = foto;
    }

    public Iterator<EstadisticaModalitat> iteEstadistiques()
    {
        return estadistiques.iterator();
    }

    public EstadisticaModalitat getEstadisticaByIndex(int index)
    {
        return estadistiques.get(index);
    }

    private final void setEstadistiques(List<EstadisticaModalitat> estadistiques)
    {
        this.estadistiques = estadistiques;
    }

    public boolean isActiu()
    {
        return actiu;
    }

    public void setActiu(boolean actiu)
    {
        this.actiu = actiu;
    }

    @Override
    public String toString()
    {
        return "Soci{" + "id=" + id + ", nif=" + nif + ", nom=" + nom + ", cognom1=" + cognom1 + ", cognom2=" + cognom2 + ", actiu=" + actiu + '}';
    }

    private boolean validarNIF(String nif)
    {
        boolean correcto = false;
        Pattern pattern = Pattern.compile("(\\d{1,8})([TRWAGMYFPDXBNJZSQVHLCKEtrwagmyfpdxbnjzsqvhlcke])");
        Matcher matcher = pattern.matcher(nif);

        if (matcher.matches()) {

            String letra = matcher.group(2);
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";

            int index = Integer.parseInt(matcher.group(1));
            index = index % 23;

            String reference = letras.substring(index, index + 1);
            if (reference.equalsIgnoreCase(letra)) {
                correcto = true;
            } else {
                correcto = false;
            }

        } else {
            correcto = false;
        }

        return correcto;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Soci other = (Soci) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
}

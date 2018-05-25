package info.infomila.billar.ipersistence;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class BillarFactory
{
    private BillarFactory()
    {
    }

    public static IBillar getInstance(String nomClasse) throws BillarException
    {
        return getInstance(nomClasse, null);
    }

    public static IBillar getInstance(String nomClasse, String nomFitxerPropietats) throws BillarException
    {
        IBillar obj;
        if (nomClasse == null) {
            throw new BillarException("Nom de la classe erroni. No pot ser null");
        }
        
        try {
            if (nomFitxerPropietats == null) {
                obj = (IBillar) Class.forName(nomClasse).newInstance();
            } else {
                Class c = Class.forName(nomClasse);
                Constructor co = c.getConstructor(String.class);
                obj = (IBillar) co.newInstance(nomFitxerPropietats);
            }
            return obj;
        } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new BillarException("No es pot crear l'objecte de la classe " + nomClasse, ex);
        }
    }
}

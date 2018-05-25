package info.infomila.billar.ipersistence;

import info.infomila.billar.models.Soci;
import java.util.List;

public interface IBillar
{
    boolean login(Soci soci) throws BillarException;
    
    void commit() throws BillarException;
    
    void rollback() throws BillarException;
    
    void close() throws BillarException;
}

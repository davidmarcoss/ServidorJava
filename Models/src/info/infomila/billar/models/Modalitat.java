package info.infomila.billar.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Access(AccessType.FIELD)
@Table(name = "modalitats")
public class Modalitat implements Serializable
{
    @Id 
    @TableGenerator(name = "gen_modalitat",
            table = "comptadors",
            pkColumnName = "clau",
            pkColumnValue = "modalitats",
            valueColumnName = "next_val",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "gen_modalitat")
    private int id;
    
    @Basic(optional = false)
    @Column(nullable = false, length = 255)
    private String descripcio;
    
    protected Modalitat() {}
    
    public Modalitat(int id, String descripcio)
    {
        setId(id);
        setDescripcio(descripcio);
    }
    
    public int getId()
    {
        return id;
    }

    protected final void setId(int id)
    {
        this.id = id;
    }

    public String getDescripcio()
    {
        return descripcio;
    }

    public final void setDescripcio(String descripcio)
    {
        if (descripcio != null && !"".equals(descripcio))
        {
            this.descripcio = descripcio;
        }
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 79 * hash + this.id;
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
        final Modalitat other = (Modalitat) obj;
        return this.id == other.id;
    }

    @Override
    public String toString()
    {
        return descripcio;
    }
    
}

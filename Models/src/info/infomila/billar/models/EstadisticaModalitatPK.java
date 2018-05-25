package info.infomila.billar.models;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class EstadisticaModalitatPK implements Serializable
{
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "soci_id")
    private Soci soci;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "modalitat_id")
    private Modalitat modalitat;
    
    protected EstadisticaModalitatPK() {}

    public EstadisticaModalitatPK(Soci soci, Modalitat modalitat)
    {
        setSoci(soci);
        setModalitat(modalitat);
    }
    
    public Soci getSoci()
    {
        return soci;
    }

    protected final void setSoci(Soci soci)
    {
        if (this.soci == null && soci != null) 
        {
            this.soci = soci;
        }
    }

    public Modalitat getModalitat()
    {
        return modalitat;
    }

    protected final void setModalitat(Modalitat modalitat)
    {
        if (this.modalitat == null && modalitat != null)
        {
            this.modalitat = modalitat;
        }
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.soci);
        hash = 79 * hash + Objects.hashCode(this.modalitat);
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
        final EstadisticaModalitatPK other = (EstadisticaModalitatPK) obj;
        if (!Objects.equals(this.soci, other.soci)) {
            return false;
        }
        return Objects.equals(this.modalitat, other.modalitat);
    }

    @Override
    public String toString()
    {
        return modalitat.toString();
    }
}

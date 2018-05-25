package info.infomila.billar.models;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Access(AccessType.FIELD)
@Table(name = "estadistiques_modalitat")
public class EstadisticaModalitat implements Serializable
{
    @EmbeddedId
    private EstadisticaModalitatPK emPK;
    
    @Basic(optional = false)
    @Column(nullable = false, name = "coeficient_base")
    private double coeficientBase;
    
    @Basic(optional = false)
    @Column(nullable = false, name = "caramboles_temporada_actual")
    private int carambolesTemporadaActual;
    
    @Basic(optional = false)
    @Column(nullable = false, name = "entrades_temporada_actual")
    private int entradesTemporadaActual;

    protected EstadisticaModalitat() {}
    
    public EstadisticaModalitat(Soci soci, Modalitat modalitat, double coeficientBase, int carambolesTemporadaActual, int entradesTemporadaActual)
    {
        emPK = new EstadisticaModalitatPK(soci, modalitat);
        setCoeficientBase(coeficientBase);
        setCarambolesTemporadaActual(carambolesTemporadaActual);
        setEntradesTemporadaActual(entradesTemporadaActual);
    }
    
    public Soci getSoci()
    {
        return emPK.getSoci();
    }

    protected final void setSoci(Soci soci)
    {
        emPK.setSoci(soci);
    }

    public Modalitat getModalitat()
    {
        return emPK.getModalitat();
    }

    protected final void setModalitat(Modalitat modalitat)
    {
        emPK.setModalitat(modalitat);
    }

    public double getCoeficientBase()
    {
        return coeficientBase;
    }

    public final void setCoeficientBase(double coeficientBase)
    {
        this.coeficientBase = coeficientBase;
    }

    public int getCarambolesTemporadaActual()
    {
        return carambolesTemporadaActual;
    }

    public final void setCarambolesTemporadaActual(int carambolesTemporadaActual)
    {
        this.carambolesTemporadaActual = carambolesTemporadaActual;
    }

    public int getEntradesTemporadaActual()
    {
        return entradesTemporadaActual;
    }

    public final void setEntradesTemporadaActual(int entradesTemporadaActual)
    {
        this.entradesTemporadaActual = entradesTemporadaActual;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.emPK);
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
        final EstadisticaModalitat other = (EstadisticaModalitat) obj;
        return Objects.equals(this.emPK, other.emPK);
    }

    @Override
    public String toString()
    {
        return emPK.toString();
    }
}

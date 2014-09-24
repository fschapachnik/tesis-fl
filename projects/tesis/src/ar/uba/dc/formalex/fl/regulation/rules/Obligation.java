package ar.uba.dc.formalex.fl.regulation.rules;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ar.uba.dc.formalex.fl.bgtheory.BGUtil;
import ar.uba.dc.formalex.fl.regulation.formula.FLFormula;
import ar.uba.dc.formalex.fl.regulation.formula.connectors.FLAnd;
import ar.uba.dc.formalex.fl.regulation.formula.connectors.FLNeg;
import ar.uba.dc.formalex.fl.regulation.formula.connectors.FLOr;
import ar.uba.dc.formalex.fl.regulation.permission.Permission;


public class Obligation extends FLFormula{
    private FLFormula formula;
    private FLFormula repair;    

    public Obligation(FLFormula formula) {
        this(formula, null);
    }

    public Obligation(FLFormula formula, FLFormula repair) {
        this.formula = formula;
        this.repair = repair;
        this.exceptions = new HashSet<FLFormula>();
    }

    public Boolean hasRepair(){
        return repair != null;
    }

    @Override
    public String toString() {
        //    O( fórmula ) repaired by  rep → G ( !fórmula -> rep)
        //    O( fórmula ) → G ( fórmula )
        if (hasRepair())
            return "G ( !" + formula.toString() + " -> (" + repair.toString() + ") )";
        else

            return "G ( " + formula.toString() + " )";
    }

    @Override
    public FLFormula instanciar(String variable, String agente, BGUtil bgUtil) {
        FLFormula rep = null;
        FLFormula f = formula.instanciar(variable, agente, bgUtil);
        if (f == null)
            return null;
        if (repair != null)
            rep = repair.instanciar(variable, agente, bgUtil);
        if (exceptions != null && !exceptions.isEmpty()){ // se realiza la conjunción de todos los Permisos que son excepciones a la regla
        	Iterator<FLFormula> exceptionsFormulas = exceptions.iterator();
        	FLFormula exceptionFormAnd = exceptionsFormulas.next();
        	FLFormula exceptionFormOr = exceptionsFormulas.next(); 
        	while (exceptionsFormulas.hasNext()){
        		FLFormula next = exceptionsFormulas.next();
        		exceptionFormAnd = new FLAnd(exceptionFormAnd, next);
        		exceptionFormOr = new FLOr(exceptionFormOr, next);
        	}
        	//instancio con los agentes la conjunción de los Permisos que representan la excepción.
        	FLFormula exceptionFormInst = exceptionFormOr.instanciar(variable, agente, bgUtil);
        	//return new Forbidden(new FLAnd(new FLNeg(f), new FLNeg(exceptionFormInst)), rep);        	
        	return new Obligation(new FLOr(f, exceptionFormInst), rep);
        }else{
        	return new Obligation(f, rep);
        }        	        
    }

	public FLFormula getFormula() {
		return formula;
	}

	public void setFormula(FLFormula formula) {
		this.formula = formula;
	}	       
}

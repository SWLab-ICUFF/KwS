/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inf.puc.rio.br.Model;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author angelo
 */
public class Pattern {
    
    private String pattern;
    private Set<String> keywordLabel;
    private Set<String> keywordGeneral;
    
    public Pattern(){
        
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * @return the keywordLabel
     */
    public Set<String> getKeywordLabel() {
        return keywordLabel;
    }

    /**
     * @param keywordLabel the keywordLabel to set
     */
    public void setKeywordLabel(Set<String> keywordLabel) {
        this.keywordLabel = keywordLabel;
    }

    /**
     * @return the keywordGeneral
     */
    public Set<String> getKeywordGeneral() {
        return keywordGeneral;
    }

    /**
     * @param keywordGeneral the keywordGeneral to set
     */
    public void setKeywordGeneral(Set<String> keywordGeneral) {
        this.keywordGeneral = keywordGeneral;
    }
    
    


    
    
    
    
    
    
}

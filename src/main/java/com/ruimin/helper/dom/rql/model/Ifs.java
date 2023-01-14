// Generated on Sun Nov 13 21:42:10 HKT 2022
// DTD/Schema  :    http://sqlmap.rql.org/rql-mapper
package com.ruimin.helper.dom.rql.model;


import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.Required;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * http://sqlmap.rql.org/rql-mapper:ifsElemType interface.
 *
 * @author shiwei
 */
public interface Ifs extends DomElement {

    /**
     * Returns the value of the if child.
     *
     * @return the value of the if child.
     */
    @NotNull
    @Required
    If getIf();


    /**
     * Returns the list of elseif children.
     *
     * @return the list of elseif children.
     */
    @NotNull
    List<If> getElseifs();

    /**
     * Adds new child to the list of elseif children.
     *
     * @return created child
     */
    If addElseif();


    /**
     * Returns the value of the else child.
     *
     * @return the value of the else child.
     */
    @NotNull
    GenericDomValue<String> getElse();


}

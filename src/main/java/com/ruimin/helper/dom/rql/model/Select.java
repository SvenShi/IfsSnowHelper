// Generated on Sun Nov 13 21:42:10 HKT 2022
// DTD/Schema  :    http://sqlmap.rql.org/rql-mapper
package com.ruimin.helper.dom.rql.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * http://sqlmap.rql.org/rql-mapper:selectElemType interface.
 *
 * @author shiwei
 */
public interface Select extends DomElement {

    /**
     * Returns the value of the simple content.
     *
     * @return the value of the simple content.
     */
    @NotNull
    @Required
    String getValue();

    /**
     * Sets the value of the simple content.
     *
     * @param value the new value to set
     */
    void setValue(@NotNull String value);


    /**
     * Returns the value of the id child.
     *
     * @return the value of the id child.
     */
    @NotNull GenericAttributeValue<String> getId();


    /**
     * Returns the value of the paramType child.
     *
     * @return the value of the paramType child.
     */
    @NotNull
    GenericAttributeValue<String> getParamType();


    /**
     * Returns the value of the resultType child.
     *
     * @return the value of the resultType child.
     */
    @NotNull GenericAttributeValue<String> getResultType();


    /**
     * Returns the list of ifs children.
     *
     * @return the list of ifs children.
     */
    @NotNull List<Ifs> getIfses();

    /**
     * Adds new child to the list of ifs children.
     *
     * @return created child
     */
    Ifs addIfs();


    /**
     * Returns the list of if children.
     *
     * @return the list of if children.
     */
    @NotNull List<If> getIfs();

    /**
     * Adds new child to the list of if children.
     *
     * @return created child
     */
    If addIf();


}

// Generated on Sun Nov 13 21:42:10 HKT 2022
// DTD/Schema  :    http://sqlmap.rql.org/rql-mapper
package com.ruimin.helper.rqlx.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * http://sqlmap.rql.org/rql-mapper:selectElemType interface.
 *
 * @author shiwei
 */
public interface Select extends DomElement,Rql {


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


}

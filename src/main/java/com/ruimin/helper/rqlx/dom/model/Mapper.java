// Generated on Sun Nov 13 21:42:10 HKT 2022
// DTD/Schema  :    http://sqlmap.rql.org/rql-mapper
package com.ruimin.helper.rqlx.dom.model;


import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Namespace;
import com.intellij.util.xml.SubTagsList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * http://sqlmap.rql.org/rql-mapper:mapperElemType interface.
 *
 * @author shiwei
 */
@Namespace("RqlxXml")
public interface Mapper extends DomElement {

    String TAG_NAME = "mapper";

    /**
     * Returns the list of insert children.
     *
     * @return the list of insert children.
     */
    @NotNull
    List<Insert> getInserts();

    /**
     * Adds new child to the list of insert children.
     *
     * @return created child
     */
    Rql addInsert();


    /**
     * Returns the list of delete children.
     *
     * @return the list of delete children.
     */
    @NotNull
    List<Delete> getDeletes();

    /**
     * Adds new child to the list of delete children.
     *
     * @return created child
     */
    Rql addDelete();


    /**
     * Returns the list of update children.
     *
     * @return the list of update children.
     */
    @NotNull
    List<Update> getUpdates();

    /**
     * Adds new child to the list of update children.
     *
     * @return created child
     */
    Rql addUpdate();


    /**
     * Returns the list of select children.
     *
     * @return the list of select children.
     */
    @NotNull
    List<Select> getSelects();

    /**
     * Adds new child to the list of select children.
     *
     * @return created child
     */
    Select addSelect();


    /**
     * Returns the list of ddl children.
     *
     * @return the list of ddl children.
     */
    @NotNull
    List<Rql> getDdls();

    /**
     * Adds new child to the list of ddl children.
     *
     * @return created child
     */
    Rql addDdl();


    @SubTagsList({"insert", "update", "delete", "select", "ddl"})
    List<Rql> getRqls();


}

// Generated on Sat Jul 23 02:59:34 CST 2022
// DTD/Schema  :    http://www.rmitec.cn/dtst-mapper

package com.ruimin.helper.dtst.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

/**
 * http://www.rmitec.cn/dtst-mapper:CommandsElemType interface.
 * @author shiwei
 */
public interface Commands extends DomElement {

	/**
	 * Returns the list of Command children.
	 * <pre>
	 * <h3>Element http://www.rmitec.cn/dtst-mapper:Command documentation</h3>
	 * 操作信息
	 * </pre>
	 * @return the list of Command children.
	 */
	@NotNull
	@SubTagList("Command")
	java.util.List<Command> getCommands();
	/**
	 * Adds new child to the list of Command children.
	 * @return created child
	 */
	@SubTagList("Command")
	Command addCommand();


}

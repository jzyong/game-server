/**
* 工具自动生成,暂时不支持泛型和对象
* @author JiangZhiYong
* @date ${date}
*/
package ${package};

import java.util.List;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import com.jjy.game.model.mongo.IConfigChecker;

/**
 *
 * @date ${date}
 */
@Entity(value = "${tableName}", noClassnameStored = true)
public class ${className} implements IConfigChecker{

<#list fieldObjects as fm>
	<#if fm.fieldName="id">
	@Id
	@Indexed
	/**${fm.description}*/
	private ${fm.fieldType} ${fm.fieldName};
	<#else>
	/**${fm.description}*/
	private ${fm.fieldType} ${fm.fieldName};
	</#if>
</#list>

<#list fieldObjects as fm>
	/**${fm.description}*/
	public ${fm.fieldType} get${fm.fieldNameUpFirst}(){
		return this.${fm.fieldName};
	}
	
	public void set${fm.fieldNameUpFirst}(${fm.fieldType} ${fm.fieldName}){
		this.${fm.fieldName}=${fm.fieldName};
	}
</#list>

}

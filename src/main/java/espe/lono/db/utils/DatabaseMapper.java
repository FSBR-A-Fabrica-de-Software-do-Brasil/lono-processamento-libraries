package espe.lono.db.utils;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;

public class DatabaseMapper<T> {
    public List<T> mapResultToObject(ResultSet resultSet, Class outputClass) {
        List<T> outputList = new ArrayList<T>();
        if ( resultSet == null ) return null;
        if ( !outputClass.isAnnotationPresent(Entity.class) ) return null;

        try {
            // Get the resultset metadata
            ResultSetMetaData metaData = resultSet.getMetaData();

            // Get all the attributes of outputClass
            Field[] fields = outputClass.getDeclaredFields();
            while (resultSet.next()) {
                T bean = (T) outputClass.newInstance();
                for (int _iterator = 0; _iterator < metaData.getColumnCount(); _iterator++) {
                    // getting the SQL column name
                    String columnName = metaData.getColumnName(_iterator + 1);
                    // reading the value of the SQL column
                    Object columnValue = resultSet.getObject(_iterator + 1);
                    // iterating over outputClass attributes to check if any attribute has 'Column' annotation with matching 'name' value
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(Column.class)) {
                            Column column = field.getAnnotation(Column.class);
                            if (column.name().equalsIgnoreCase(columnName) && columnValue != null) {
                                BeanUtils.setProperty(bean, field.getName(), columnValue);
                                break;
                            }
                        }
                    }
                }

                outputList.add(bean);
            }
        } catch (IllegalAccessException | SQLException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return outputList;
    }
}

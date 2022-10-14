/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.ui.models;

import ch.poudriere.bouboutel.bundles.I18n;
import ch.poudriere.bouboutel.models.AbstractModel;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

/**
 *
 * @author Hervé Martinet <herve.martinet@gmail.com>
 * @param <T>
 */
public class DataTableModelColumn<T extends AbstractModel> {
    private String name;
    private String label = null;
    private Class clazz;
    private Function<T, Object> getter;
    private ModelSetter<T, Object> setter;
    private boolean editable = true;

    private DataTableModelColumn(String name) {
        this.name = name;
        this.label = I18n.get("label.%s".formatted(name));
    }

    public DataTableModelColumn(PropertyDescriptor pd) {
        this(pd.getName());
        this.clazz = pd.getPropertyType();
        this.getter = (p) -> {
            try {
                return pd.getReadMethod().invoke(p);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new Error("No getter for property %s".formatted(
                        pd.getName()));
            }
        };
        this.setter = (p, value) -> {
            try {
                pd.getWriteMethod().invoke(p, value);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                this.editable = false;
            }
        };
    }

    public DataTableModelColumn(String name, Class clazz,
            Function<T, Object> getter,
            ModelSetter<T, Object> setter) {
        this(name);
        this.clazz = clazz;
        this.getter = getter;
        this.setter = setter;
    }

    public DataTableModelColumn(String name, Class clazz,
            Function<T, Object> getter) {
        this(name, clazz, getter, null);
        this.editable = false;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the clazz
     */
    public Class getClazz() {
        return clazz;
    }

    /**
     * @return the getter
     */
    public Function<T, Object> getGetter() {
        return getter;
    }

    /**
     * @return the setter
     */
    public ModelSetter<T, Object> getSetter() {
        return setter;
    }

    /**
     * @return the editable
     */
    public boolean isEditable() {
        return editable;
    }
}

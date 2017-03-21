package ch.datascience.typesystem.model;

/**
 * Created by johann on 17/03/17.
 */
public enum DataType {

    STRING,
    CHARACTER,
    BOOLEAN,
    BYTE,
    SHORT,
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE;
//    Decimal
//    Precision
//    Date
//    Geoshape
//    UUID

    public Class<?> javaClass() {
        switch (this) {
            case STRING:
                return java.lang.String.class;
            case CHARACTER:
                return java.lang.Character.class;
            case BOOLEAN:
                return java.lang.Boolean.class;
            case BYTE:
                return java.lang.Byte.class;
            case SHORT:
                return java.lang.Short.class;
            case INTEGER:
                return java.lang.Integer.class;
            case LONG:
                return java.lang.Long.class;
            case FLOAT:
                return java.lang.Float.class;
            case DOUBLE:
                return java.lang.Double.class;
            default:
                throw new RuntimeException("No java class mapping for: " + this.name());
        }
    }

}

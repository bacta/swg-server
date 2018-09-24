package io.bacta.swg.utility;

/**
 * Created by crush on 4/19/2016.
 */
public class DynamicVariableParamData {

    private String name;
    private DataType type;
    //private union data { IntegerParam, FloatParam, StringParam, List<DynamicVariableParamData> }


    public enum DataType {
        Unknown(0),
        Integer(1),
        Float(2),
        String(3),
        List(4);

        private static final DataType[] values = values();
        public final int value;

        DataType(final int value) {
            this.value = value;
        }

        public DataType from(int value) {
            for (final DataType dataType : values) {
                if (dataType.value == value)
                    return dataType;
            }

            throw new IllegalStateException("UNKNOWN data type.");
        }
    }
}

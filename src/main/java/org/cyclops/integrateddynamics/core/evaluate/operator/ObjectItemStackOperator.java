package org.cyclops.integrateddynamics.core.evaluate.operator;

import net.minecraft.item.ItemStack;
import org.cyclops.integrateddynamics.core.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.core.evaluate.variable.*;

/**
 * Base class for block object operators.
 * @author rubensworks
 */
public class ObjectItemStackOperator extends ObjectOperatorBase {

    public ObjectItemStackOperator(String name, IFunction function) {
        this(name, name, 2, function, IConfigRenderPattern.INFIX);
    }

    public ObjectItemStackOperator(String symbol, String operatorName, IFunction function) {
        this(symbol, operatorName, 2, function, IConfigRenderPattern.INFIX);
    }

    public ObjectItemStackOperator(String symbol, String operatorName, IFunction function, IConfigRenderPattern renderPattern) {
        this(symbol, operatorName, 2, function, renderPattern);
    }

    public ObjectItemStackOperator(String symbol, String operatorName, int inputLength, IFunction function, IConfigRenderPattern renderPattern) {
        super(symbol, operatorName, constructInputVariables(inputLength, ValueTypes.OBJECT_ITEMSTACK), ValueTypes.OBJECT_ITEMSTACK, function, renderPattern);
    }

    protected ObjectItemStackOperator(String name, IValueType[] inputTypes, IValueType outputType,
                                      IFunction function, IConfigRenderPattern renderPattern) {
        this(name, name, inputTypes, outputType, function, renderPattern);
    }

    protected ObjectItemStackOperator(String symbol, String operatorName, IValueType[] inputTypes, IValueType outputType,
                                      IFunction function, IConfigRenderPattern renderPattern) {
        super(symbol, operatorName, inputTypes, outputType, function, renderPattern);
    }

    @Override
    public String getUnlocalizedObjectType() {
        return "itemstack";
    }

    public static ObjectItemStackOperator toInt(String name, final IIntegerFunction function) {
        return toInt(name, function, 0);
    }

    public static ObjectItemStackOperator toInt(String name, final IIntegerFunction function, final int defaultValue) {
        return new ObjectItemStackOperator(name, new IValueType[]{ValueTypes.OBJECT_ITEMSTACK}, ValueTypes.INTEGER, new OperatorBase.IFunction() {
            @Override
            public IValue evaluate(IVariable... variables) throws EvaluationException {
                ItemStack a = ((ValueObjectTypeItemStack.ValueItemStack) variables[0].getValue()).getRawValue();
                return ValueTypeInteger.ValueInteger.of(a == null ? defaultValue : function.evaluate(a));
            }
        }, IConfigRenderPattern.SUFFIX_1_LONG);
    }

    public static ObjectItemStackOperator toBoolean(String name, final IBooleanFunction function) {
        return toBoolean(name, function, false);
    }

    public static ObjectItemStackOperator toBoolean(String name, final IBooleanFunction function, final boolean defaultValue) {
        return new ObjectItemStackOperator(name, new IValueType[]{ValueTypes.OBJECT_ITEMSTACK}, ValueTypes.BOOLEAN, new OperatorBase.IFunction() {
            @Override
            public IValue evaluate(IVariable... variables) throws EvaluationException {
                ItemStack a = ((ValueObjectTypeItemStack.ValueItemStack) variables[0].getValue()).getRawValue();
                return ValueTypeBoolean.ValueBoolean.of(a == null ? defaultValue : function.evaluate(a));
            }
        }, IConfigRenderPattern.SUFFIX_1_LONG);
    }

    public static interface IIntegerFunction {
        public int evaluate(ItemStack itemStack) throws EvaluationException;
    }

    public static interface IBooleanFunction {
        public boolean evaluate(ItemStack itemStack) throws EvaluationException;
    }

}

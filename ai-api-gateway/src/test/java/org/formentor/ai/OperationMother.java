package org.formentor.ai;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;

import java.util.Set;

public class OperationMother {
    public static Operation fixed() {
        var operation = new Operation();
        operation.setOperationId("getHotelsByDestination");
        operation.setDescription("Get hotels for a given destination");

        var parameter = new Parameter();
        parameter.setName("destination");
        parameter.setDescription("Destination where hotels are located");
        var schema = new Schema<>();
        schema.setTypes(Set.of("string"));
        parameter.setSchema(schema);
        parameter.setExample("Madrid, Paris");
        parameter.setRequired(true);
        operation.addParametersItem(parameter);

        return operation;
    }
}

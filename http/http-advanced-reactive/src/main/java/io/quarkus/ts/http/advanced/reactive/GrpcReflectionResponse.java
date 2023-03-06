package io.quarkus.ts.http.advanced.reactive;

import java.util.ArrayList;
import java.util.List;

import io.grpc.reflection.v1.ServerReflectionResponse;
import io.grpc.reflection.v1.ServiceResponse;

public final class GrpcReflectionResponse {

    private ServerReflectionResponse response;

//    public GrpcReflectionResponse(ServerReflectionResponse response) {
//        this.response = response;
//    }

    public GrpcReflectionResponse() {
    }

    void initGrpcReflectionResponse(ServerReflectionResponse response) {
        this.response = response;
    }

    public List<String> getServiceList() {
        List<ServiceResponse> serviceList = response.getListServicesResponse().getServiceList();
        List<String> serviceNames = new ArrayList<>();
        for (var service : serviceList) {
            serviceNames.add(service.getName());
        }
        return serviceNames;
    }

    public int getServiceCount() {
        return response.getListServicesResponse().getServiceCount();
    }

    public String getFileDescriptor() {
        return response.getFileDescriptorResponse().toString();
    }

}

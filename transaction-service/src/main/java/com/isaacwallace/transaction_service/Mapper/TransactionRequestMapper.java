package com.isaacwallace.transaction_service.Mapper;

import com.isaacwallace.transaction_service.DataAccess.Transaction;
import com.isaacwallace.transaction_service.DataAccess.TransactionIdentifier;
import com.isaacwallace.transaction_service.Presentation.Models.TransactionRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TransactionRequestMapper {
    @Mapping(target = "id", ignore = true)
    Transaction requestModelToEntity(TransactionRequestModel transactionRequestModel, TransactionIdentifier transactionIdentifier);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(TransactionRequestModel transactionRequestModel, @MappingTarget Transaction transaction);
}
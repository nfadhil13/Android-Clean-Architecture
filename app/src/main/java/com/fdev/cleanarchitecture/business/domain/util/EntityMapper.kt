package com.fdev.cleanarchitecture.business.domain.util

interface EntityMapper <Entity , DomainModel> {
    fun mapFromEntity(entity : Entity) : DomainModel
    fun mapToEntity(domainModel: DomainModel) : Entity
}
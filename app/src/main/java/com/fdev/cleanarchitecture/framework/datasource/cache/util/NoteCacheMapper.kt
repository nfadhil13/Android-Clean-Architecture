package com.fdev.cleanarchitecture.framework.datasource.cache.util

import com.fdev.cleanarchitecture.business.domain.model.Note
import com.fdev.cleanarchitecture.business.domain.util.DateUtil
import com.fdev.cleanarchitecture.business.domain.util.EntityMapper
import com.fdev.cleanarchitecture.framework.datasource.cache.model.NoteCacheEntity
import javax.inject.Inject

class NoteCacheMapper
@Inject
constructor(
    private val dateUtil: DateUtil
) : EntityMapper<NoteCacheEntity , Note>{

    fun entityListToNoteList(entities : List<NoteCacheEntity>): List<Note> {
        val list : ArrayList<Note> = ArrayList()
        for(entity in entities){
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun noteListToEntityList(notes : List<Note>): List<NoteCacheEntity> {
        val list : ArrayList<NoteCacheEntity> = ArrayList()
        for(note in notes){
            list.add(mapToEntity(note))
        }
        return list
    }

    override fun mapFromEntity(entity: NoteCacheEntity): Note {
        return Note(
            id = entity.id,
            title = entity.title,
            body = entity.body,
            created_at = entity.created_at,
            updated_at = entity.updated_at
        )
    }

    override fun mapToEntity(domainModel: Note): NoteCacheEntity {
        return NoteCacheEntity(
            id = domainModel.id,
            title = domainModel.title,
            body = domainModel.body,
            created_at = domainModel.created_at,
            updated_at = domainModel.updated_at
        )
    }
}
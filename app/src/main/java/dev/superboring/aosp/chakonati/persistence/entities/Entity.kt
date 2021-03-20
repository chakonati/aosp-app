package dev.superboring.aosp.chakonati.persistence.entities

import dev.superboring.aosp.chakonati.persistence.dao.DaoBase
import dev.superboring.aosp.chakonati.persistence.dao.SingleEntryDao

interface Entity<T : DaoBase>

interface SingleEntryEntity<T : SingleEntryDao<*>> : Entity<T>

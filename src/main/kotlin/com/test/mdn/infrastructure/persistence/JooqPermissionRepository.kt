package com.test.mdn.infrastructure.persistence

import com.test.mdn.domain.model.Permission
import com.test.mdn.domain.repository.PermissionRepository
import com.test.mdn.infrastructure.persistence.jooq.generated.Tables.PERMISSIONS
import com.test.mdn.infrastructure.persistence.jooq.generated.tables.records.PermissionsRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqPermissionRepository(
    private val dslContext: DSLContext,
) : PermissionRepository {

    override fun findById(id: Long): Permission? {
        val permissionsRecord: PermissionsRecord = dslContext
            .selectFrom(PERMISSIONS)
            .where(PERMISSIONS.ID.eq(id))
            .fetchOne()
            ?: return null

        return toPermission(permissionsRecord)
    }

    override fun findByName(name: String): Permission? {
        val permissionsRecord: PermissionsRecord = dslContext
            .selectFrom(PERMISSIONS)
            .where(PERMISSIONS.NAME.eq(name))
            .fetchOne()
            ?: return null

        return toPermission(permissionsRecord)
    }

    override fun findAll(): List<Permission> {
        return dslContext
            .selectFrom(PERMISSIONS)
            .orderBy(PERMISSIONS.NAME)
            .fetch()
            .map { permissionsRecord -> toPermission(permissionsRecord) }
    }

    override fun save(permission: Permission): Permission {
        val permissionId = permission.id

        val savedRecord: PermissionsRecord = if (permissionId == null) {
            insertPermission(permission)
        } else {
            updatePermission(permissionId, permission)
        }

        return toPermission(savedRecord)
    }

    private fun insertPermission(permission: Permission): PermissionsRecord {
        return dslContext
            .insertInto(PERMISSIONS)
            .set(PERMISSIONS.NAME, permission.name)
            .set(PERMISSIONS.DESCRIPTION, permission.description)
            .returning()
            .fetchSingle()
    }

    private fun updatePermission(permissionId: Long, permission: Permission): PermissionsRecord {
        return dslContext
            .update(PERMISSIONS)
            .set(PERMISSIONS.NAME, permission.name)
            .set(PERMISSIONS.DESCRIPTION, permission.description)
            .where(PERMISSIONS.ID.eq(permissionId))
            .returning()
            .fetchSingle()
    }

    private fun toPermission(permissionsRecord: PermissionsRecord): Permission {
        return Permission(
            id = permissionsRecord.id,
            name = permissionsRecord.name,
            description = permissionsRecord.description,
        )
    }
}
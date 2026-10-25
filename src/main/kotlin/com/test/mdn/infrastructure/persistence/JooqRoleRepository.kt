package com.test.mdn.infrastructure.persistence

import com.test.mdn.domain.model.Permission
import com.test.mdn.domain.model.Role
import com.test.mdn.domain.repository.RoleRepository
import com.test.mdn.infrastructure.persistence.jooq.generated.Tables.PERMISSIONS
import com.test.mdn.infrastructure.persistence.jooq.generated.Tables.ROLES
import com.test.mdn.infrastructure.persistence.jooq.generated.Tables.ROLE_PERMISSIONS
import com.test.mdn.infrastructure.persistence.jooq.generated.tables.records.RolesRecord
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class JooqRoleRepository(
    private val dslContext: DSLContext,
) : RoleRepository {

    // CARI ROLE — semuanya memakai satu fungsi findRolesWhere
    override fun findById(id: Long): Role? {
        return findRolesWhere(ROLES.ID.eq(id)).firstOrNull()
    }

    override fun findByName(name: String): Role? {
        return findRolesWhere(ROLES.NAME.eq(name)).firstOrNull()
    }

    override fun findAll(): List<Role> {
        return findRolesWhere(DSL.noCondition())
    }

    // SIMPAN ROLE
    override fun save(role: Role): Role {
        val roleId = role.id

        val savedRecord: RolesRecord = if (roleId == null) {
            insertRole(role)
        } else {
            updateRole(roleId, role)
        }

        return findRolesWhere(ROLES.ID.eq(savedRecord.id)).first()
    }

    override fun assignPermission(roleId: Long, permissionId: Long) {
        dslContext
            .insertInto(ROLE_PERMISSIONS)
            .set(ROLE_PERMISSIONS.ROLE_ID, roleId)
            .set(ROLE_PERMISSIONS.PERMISSION_ID, permissionId)
            .onConflictDoNothing()
            .execute()
    }

    // FUNGSI BANTU
    private fun insertRole(role: Role): RolesRecord {
        return dslContext
            .insertInto(ROLES)
            .set(ROLES.NAME, role.name)
            .set(ROLES.DESCRIPTION, role.description)
            .returning()
            .fetchSingle()
    }

    private fun updateRole(roleId: Long, role: Role): RolesRecord {
        return dslContext
            .update(ROLES)
            .set(ROLES.NAME, role.name)
            .set(ROLES.DESCRIPTION, role.description)
            .where(ROLES.ID.eq(roleId))
            .returning()
            .fetchSingle()
    }

    private fun findRolesWhere(condition: Condition): List<Role> {
        val rowsGroupedByRoleId = dslContext
            .select(
                ROLES.ID,
                ROLES.NAME,
                ROLES.DESCRIPTION,
                PERMISSIONS.ID,
                PERMISSIONS.NAME,
                PERMISSIONS.DESCRIPTION,
            )
            .from(ROLES)
            .leftJoin(ROLE_PERMISSIONS).on(ROLE_PERMISSIONS.ROLE_ID.eq(ROLES.ID))
            .leftJoin(PERMISSIONS).on(PERMISSIONS.ID.eq(ROLE_PERMISSIONS.PERMISSION_ID))
            .where(condition)
            .orderBy(ROLES.NAME, PERMISSIONS.NAME)
            .fetchGroups(ROLES.ID)

        return rowsGroupedByRoleId.map { entry ->
            val roleId = entry.key
            val rowsOfThisRole = entry.value
            val firstRow = rowsOfThisRole.first()

            val permissions = rowsOfThisRole
                .filter { row -> row.get(PERMISSIONS.ID) != null }
                .map { row ->
                    Permission(
                        id = row.get(PERMISSIONS.ID),
                        name = row.get(PERMISSIONS.NAME),
                        description = row.get(PERMISSIONS.DESCRIPTION),
                    )
                }
                .toSet()

            Role(
                id = roleId,
                name = firstRow.get(ROLES.NAME),
                description = firstRow.get(ROLES.DESCRIPTION),
                permissions = permissions,
            )
        }
    }
}
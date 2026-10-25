package com.test.mdn.infrastructure.persistence

import com.test.mdn.domain.model.Permission
import com.test.mdn.domain.model.Role
import com.test.mdn.domain.model.User
import com.test.mdn.domain.repository.UserRepository

import com.test.mdn.infrastructure.persistence.jooq.generated.Tables.USERS
import com.test.mdn.infrastructure.persistence.jooq.generated.Tables.USER_ROLES
import com.test.mdn.infrastructure.persistence.jooq.generated.Tables.ROLES
import com.test.mdn.infrastructure.persistence.jooq.generated.Tables.PERMISSIONS
import com.test.mdn.infrastructure.persistence.jooq.generated.Tables.ROLE_PERMISSIONS
import com.test.mdn.infrastructure.persistence.jooq.generated.tables.records.UsersRecord
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class JooqUserRepository(
    private val dslContext: DSLContext
): UserRepository {
    override fun findById(id: Long): User? {
        val usersRecord: UsersRecord = dslContext
            .selectFrom(USERS)
            .where(USERS.ID.eq(id))
            .fetchOne()
            ?: return null

        val roles = findRolesByUserId(usersRecord.id)
        return toUser(usersRecord, roles)
    }

    override fun findByUsername(username: String): User? {
        val usersRecord: UsersRecord = dslContext
            .selectFrom(USERS)
            .where(USERS.USERNAME.eq(username))
            .fetchOne()
            ?: return null

        val roles = findRolesByUserId(usersRecord.id)
        return toUser(usersRecord, roles)
    }

    override fun existsByUsername(username: String): Boolean {
        return dslContext.fetchExists(USERS, USERS.USERNAME.eq(username))
    }

    override fun existsByEmail(email: String): Boolean {
        return dslContext.fetchExists(USERS, USERS.EMAIL.eq(email))
    }

    override fun save(user: User): User {
        val userId = user.id
        val savedRecord: UsersRecord = if(userId == null) {
            insertUser(user)
        }else {
            updateUser(userId, user)
        }
        val roles = findRolesByUserId(savedRecord.id)
        return toUser(savedRecord, roles)
    }

    override fun assignRole(userId: Long, roleId: Long) {
        dslContext
            .insertInto(USER_ROLES)
            .set(USER_ROLES.USER_ID, userId)
            .set(USER_ROLES.ROLE_ID, roleId)
            .onConflictDoNothing()
            .execute()
    }

    // private method
    private fun insertUser(user: User): UsersRecord {
        return dslContext
            .insertInto(USERS)
            .set(USERS.USERNAME, user.username)
            .set(USERS.EMAIL, user.email)
            .set(USERS.PASSWORD_HASH, user.passwordHash)
            .set(USERS.ENABLED, user.enabled)
            .returning()
            .fetchSingle()
    }

    private fun updateUser(userId: Long, user: User): UsersRecord {
        return dslContext
            .update(USERS)
            .set(USERS.USERNAME, user.username)
            .set(USERS.EMAIL, user.email)
            .set(USERS.PASSWORD_HASH, user.passwordHash)
            .set(USERS.ENABLED, user.enabled)
            .set(USERS.UPDATED_AT, DSL.currentOffsetDateTime())
            .where(USERS.ID.eq(userId))
            .returning()
            .fetchSingle()
    }

    private fun findRolesByUserId(userId: Long): Set<Role> {
        val rowsGroupedByRoleId = dslContext
            .select(
                ROLES.ID,
                ROLES.NAME,
                ROLES.DESCRIPTION,
                PERMISSIONS.ID,
                PERMISSIONS.NAME,
                PERMISSIONS.DESCRIPTION
            )
            .from(USER_ROLES)
            .join(ROLES).on(ROLES.ID.eq(USER_ROLES.ROLE_ID))
            .leftJoin(ROLE_PERMISSIONS).on(ROLE_PERMISSIONS.ROLE_ID.eq(ROLES.ID))
            .leftJoin(PERMISSIONS).on(PERMISSIONS.ID.eq(ROLE_PERMISSIONS.PERMISSION_ID))
            .where(USER_ROLES.USER_ID.eq(userId))
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
        }.toSet()
    }

    private fun toUser(usersRecord: UsersRecord, roles: Set<Role>): User {
        return User(
            id = usersRecord.id,
            username = usersRecord.username,
            email = usersRecord.email,
            passwordHash = usersRecord.passwordHash,
            enabled = usersRecord.enabled,
            roles = roles,
            createdAt = usersRecord.createdAt,
            updatedAt = usersRecord.updatedAt
        )
    }


}
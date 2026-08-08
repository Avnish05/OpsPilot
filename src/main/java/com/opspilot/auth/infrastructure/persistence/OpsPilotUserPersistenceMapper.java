package com.opspilot.auth.infrastructure.persistence;

import com.opspilot.auth.domain.OpsPilotUser;
import org.springframework.stereotype.Component;

@Component
class OpsPilotUserPersistenceMapper {
    OpsPilotUserJpaEntity toEntity(OpsPilotUser user) {
        return new OpsPilotUserJpaEntity(user.id(), user.email(), user.passwordHash(), user.role(), user.enabled(),
                user.createdAt(), user.updatedAt(), user.version());
    }
    OpsPilotUser toDomain(OpsPilotUserJpaEntity entity) {
        return OpsPilotUser.reconstitute(entity.id, entity.email, entity.passwordHash, entity.role, entity.enabled,
                entity.createdAt, entity.updatedAt, entity.version);
    }
}

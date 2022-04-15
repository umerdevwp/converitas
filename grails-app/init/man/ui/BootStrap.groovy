package man.ui

import com.coveritas.heracles.ui.Organization
import com.coveritas.heracles.ui.Role
import com.coveritas.heracles.ui.User

class BootStrap {

    def init = { servletContext ->
        User.withTransaction { status ->
            if (User.list().isEmpty()) {
                Date now = new Date()
                Organization org = new Organization(uuid: Organization.COVERITAS_UUID, name: "CoVeritas", created: now, lastUpdated: now).save(failOnError: true)
                Role adminRole = new Role(name: "Admin").save(failOnError: true)
                Role userRole = new Role(name: "User").save(failOnError: true)
                User.create(User.SYS_ADMIN_UUID, "admin", org, "@dm1n", [adminRole] as Set<Role>)
            }
        }
    }
    def destroy = {
    }
}

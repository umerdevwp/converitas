package man.ui

import com.coveritas.heracles.ui.Organization
import com.coveritas.heracles.ui.Role
import com.coveritas.heracles.ui.User
import grails.util.Holders
import org.springframework.context.ApplicationContext
import org.springframework.transaction.TransactionStatus
import org.apache.commons.codec.binary.Hex

import javax.servlet.ServletContext
import javax.sql.DataSource
import java.sql.Connection

//@CompileStatic
class BootStrap {

    def init = { ServletContext servletContext ->
        User.withTransaction { TransactionStatus status ->
            if (User.list().isEmpty()) {
                Date now = new Date()
                Organization org = new Organization(uuid: Organization.COVERITAS_UUID, name: "CoVeritas", created: now, lastUpdated: now).save(failOnError: true)
                Role adminRole = new Role(name: Role.ADMIN).save(failOnError: true)
                new Role(name: Role.USER).save(failOnError: true)
                User.create(User.SYS_ADMIN_UUID, "admin", org, "@dm1n", [adminRole] as Set<Role>)
            } else {
                BigInteger h = 0x00E9263726E15A04F4E2E26E96BFBECFD21EEB343E12E7F4CA8E7E1DA87B7583EC8E464B888D2CA630E5B8451AC14BDB7423879135CFC51FE471FE43101FA60EC7
                User a = User.get(1)
                BigInteger h2 = new BigInteger(a.passwordHash)
                if (h != h2) {
                    a.changePassword("@dm1n")
                    a.save([flush:true, update:true])
                    ApplicationContext ctx = Holders.grailsApplication.mainContext
                    DataSource  ds = ctx.getBean(DataSource)
                    Connection c = null
                    try {
                        c = ds.connection
                        c.createStatement().executeUpdate(
                                "UPDATE ma_user set password_hash = decode('" + Hex.encodeHexString(a.passwordHash) + "', 'hex')," +
                                        " salt = decode('" + Hex.encodeHexString(a.salt) + "', 'hex')  where id = 1"
                        )
                    } finally {
                        if (c!=null) {
                            c.close()
                        }
                    }
                }
            }
        }
    }
    def destroy = {
    }
}

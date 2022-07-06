package man.ui

import com.coveritas.heracles.ui.ApiService
import com.coveritas.heracles.ui.Color
import com.coveritas.heracles.ui.Organization
import com.coveritas.heracles.ui.Policy
import com.coveritas.heracles.ui.Project
import com.coveritas.heracles.ui.Role
import com.coveritas.heracles.ui.User
import com.coveritas.heracles.ui.View
import grails.util.Holders
import org.hibernate.dialect.Dialect
import org.hibernate.engine.jdbc.dialect.internal.StandardDialectResolver
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver
import org.hibernate.exception.JDBCConnectionException
import org.springframework.context.ApplicationContext
import org.springframework.transaction.TransactionStatus

import javax.servlet.ServletContext
import javax.sql.DataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

//@CompileStatic
class BootStrap {

    def init = { ServletContext servletContext ->
        Color adminColor = null
        Color.withTransaction { TransactionStatus status ->
            if (Color.list().isEmpty()) {
                new Color([name:"IndianRed", code: "#CD5C5C"]).save(update:false, failOnError:true)
                new Color([name:"LightCoral", code: "#F08080"]).save(update:false, failOnError:true)
                new Color([name:"Salmon", code: "#FA8072"]).save(update:false, failOnError:true)
                new Color([name:"DarkSalmon", code: "#E9967A"]).save(update:false, failOnError:true)
                new Color([name:"LightSalmon", code: "#FFA07A"]).save(update:false, failOnError:true)
                new Color([name:"Crimson", code: "#DC143C"]).save(update:false, failOnError:true)
                new Color([name:"Red", code: "#FF0000"]).save(update:false, failOnError:true)
                adminColor = new Color([name:"FireBrick", code: "#B22222"]).save(update:false, failOnError:true)
                new Color([name:"DarkRed", code: "#8B0000"]).save(update:false, failOnError:true)
                new Color([name:"Pink", code: "#FFC0CB"]).save(update:false, failOnError:true)
                new Color([name:"LightPink", code: "#FFB6C1"]).save(update:false, failOnError:true)
                new Color([name:"HotPink", code: "#FF69B4"]).save(update:false, failOnError:true)
                new Color([name:"DeepPink", code: "#FF1493"]).save(update:false, failOnError:true)
                new Color([name:"MediumVioletRed", code: "#C71585"]).save(update:false, failOnError:true)
                new Color([name:"PaleVioletRed", code: "#DB7093"]).save(update:false, failOnError:true)
//                new Color([name:"LightSalmon", code: "#FFA07A"]).save(update:false, failOnError:true)
                new Color([name:"Coral", code: "#FF7F50"]).save(update:false, failOnError:true)
                new Color([name:"Tomato", code: "#FF6347"]).save(update:false, failOnError:true)
                new Color([name:"OrangeRed", code: "#FF4500"]).save(update:false, failOnError:true)
                new Color([name:"DarkOrange", code: "#FF8C00"]).save(update:false, failOnError:true)
                new Color([name:"Orange", code: "#FFA500"]).save(update:false, failOnError:true)
                new Color([name:"Gold", code: "#FFD700"]).save(update:false, failOnError:true)
                new Color([name:"Yellow", code: "#FFFF00"]).save(update:false, failOnError:true)
                new Color([name:"LightYellow", code: "#FFFFE0"]).save(update:false, failOnError:true)
                new Color([name:"LemonChiffon", code: "#FFFACD"]).save(update:false, failOnError:true)
                new Color([name:"LightGoldenrodYellow", code: "#FAFAD2"]).save(update:false, failOnError:true)
                new Color([name:"PapayaWhip", code: "#FFEFD5"]).save(update:false, failOnError:true)
                new Color([name:"Moccasin", code: "#FFE4B5"]).save(update:false, failOnError:true)
                new Color([name:"PeachPuff", code: "#FFDAB9"]).save(update:false, failOnError:true)
                new Color([name:"PaleGoldenrod", code: "#EEE8AA"]).save(update:false, failOnError:true)
                new Color([name:"Khaki", code: "#F0E68C"]).save(update:false, failOnError:true)
                new Color([name:"DarkKhaki", code: "#BDB76B"]).save(update:false, failOnError:true)
                new Color([name:"Lavender", code: "#E6E6FA"]).save(update:false, failOnError:true)
                new Color([name:"Thistle", code: "#D8BFD8"]).save(update:false, failOnError:true)
                new Color([name:"Plum", code: "#DDA0DD"]).save(update:false, failOnError:true)
                new Color([name:"Violet", code: "#EE82EE"]).save(update:false, failOnError:true)
                new Color([name:"Orchid", code: "#DA70D6"]).save(update:false, failOnError:true)
                new Color([name:"Fuchsia", code: "#FF00FF"]).save(update:false, failOnError:true)
                new Color([name:"Magenta", code: "#FF00FF"]).save(update:false, failOnError:true)
                new Color([name:"MediumOrchid", code: "#BA55D3"]).save(update:false, failOnError:true)
                new Color([name:"MediumPurple", code: "#9370DB"]).save(update:false, failOnError:true)
                new Color([name:"RebeccaPurple", code: "#663399"]).save(update:false, failOnError:true)
                new Color([name:"BlueViolet", code: "#8A2BE2"]).save(update:false, failOnError:true)
                new Color([name:"DarkViolet", code: "#9400D3"]).save(update:false, failOnError:true)
                new Color([name:"DarkOrchid", code: "#9932CC"]).save(update:false, failOnError:true)
                new Color([name:"DarkMagenta", code: "#8B008B"]).save(update:false, failOnError:true)
                new Color([name:"Purple", code: "#800080"]).save(update:false, failOnError:true)
                new Color([name:"Indigo", code: "#4B0082"]).save(update:false, failOnError:true)
                new Color([name:"SlateBlue", code: "#6A5ACD"]).save(update:false, failOnError:true)
                new Color([name:"DarkSlateBlue", code: "#483D8B"]).save(update:false, failOnError:true)
                new Color([name:"MediumSlateBlue", code: "#7B68EE"]).save(update:false, failOnError:true)
                new Color([name:"GreenYellow", code: "#ADFF2F"]).save(update:false, failOnError:true)
                new Color([name:"Chartreuse", code: "#7FFF00"]).save(update:false, failOnError:true)
                new Color([name:"LawnGreen", code: "#7CFC00"]).save(update:false, failOnError:true)
                new Color([name:"Lime", code: "#00FF00"]).save(update:false, failOnError:true)
                new Color([name:"LimeGreen", code: "#32CD32"]).save(update:false, failOnError:true)
                new Color([name:"PaleGreen", code: "#98FB98"]).save(update:false, failOnError:true)
                new Color([name:"LightGreen", code: "#90EE90"]).save(update:false, failOnError:true)
                new Color([name:"MediumSpringGreen", code: "#00FA9A"]).save(update:false, failOnError:true)
                new Color([name:"SpringGreen", code: "#00FF7F"]).save(update:false, failOnError:true)
                new Color([name:"MediumSeaGreen", code: "#3CB371"]).save(update:false, failOnError:true)
                new Color([name:"SeaGreen", code: "#2E8B57"]).save(update:false, failOnError:true)
                new Color([name:"ForestGreen", code: "#228B22"]).save(update:false, failOnError:true)
                new Color([name:"Green", code: "#008000"]).save(update:false, failOnError:true)
                new Color([name:"DarkGreen", code: "#006400"]).save(update:false, failOnError:true)
                new Color([name:"YellowGreen", code: "#9ACD32"]).save(update:false, failOnError:true)
                new Color([name:"OliveDrab", code: "#6B8E23"]).save(update:false, failOnError:true)
                new Color([name:"Olive", code: "#808000"]).save(update:false, failOnError:true)
                new Color([name:"DarkOliveGreen", code: "#556B2F"]).save(update:false, failOnError:true)
                new Color([name:"MediumAquamarine", code: "#66CDAA"]).save(update:false, failOnError:true)
                new Color([name:"DarkSeaGreen", code: "#8FBC8B"]).save(update:false, failOnError:true)
                new Color([name:"LightSeaGreen", code: "#20B2AA"]).save(update:false, failOnError:true)
                new Color([name:"DarkCyan", code: "#008B8B"]).save(update:false, failOnError:true)
                new Color([name:"Teal", code: "#008080"]).save(update:false, failOnError:true)
                new Color([name:"Aqua", code: "#00FFFF"]).save(update:false, failOnError:true)
                new Color([name:"Cyan", code: "#00FFFF"]).save(update:false, failOnError:true)
                new Color([name:"LightCyan", code: "#E0FFFF"]).save(update:false, failOnError:true)
                new Color([name:"PaleTurquoise", code: "#AFEEEE"]).save(update:false, failOnError:true)
                new Color([name:"Aquamarine", code: "#7FFFD4"]).save(update:false, failOnError:true)
                new Color([name:"Turquoise", code: "#40E0D0"]).save(update:false, failOnError:true)
                new Color([name:"MediumTurquoise", code: "#48D1CC"]).save(update:false, failOnError:true)
                new Color([name:"DarkTurquoise", code: "#00CED1"]).save(update:false, failOnError:true)
                new Color([name:"CadetBlue", code: "#5F9EA0"]).save(update:false, failOnError:true)
                new Color([name:"SteelBlue", code: "#4682B4"]).save(update:false, failOnError:true)
                new Color([name:"LightSteelBlue", code: "#B0C4DE"]).save(update:false, failOnError:true)
                new Color([name:"PowderBlue", code: "#B0E0E6"]).save(update:false, failOnError:true)
                new Color([name:"LightBlue", code: "#ADD8E6"]).save(update:false, failOnError:true)
                new Color([name:"SkyBlue", code: "#87CEEB"]).save(update:false, failOnError:true)
                new Color([name:"LightSkyBlue", code: "#87CEFA"]).save(update:false, failOnError:true)
                new Color([name:"DeepSkyBlue", code: "#00BFFF"]).save(update:false, failOnError:true)
                new Color([name:"DodgerBlue", code: "#1E90FF"]).save(update:false, failOnError:true)
                new Color([name:"CornflowerBlue", code: "#6495ED"]).save(update:false, failOnError:true)
//                new Color([name:"MediumSlateBlue", code: "#7B68EE"]).save(update:false, failOnError:true)
                new Color([name:"RoyalBlue", code: "#4169E1"]).save(update:false, failOnError:true)
                new Color([name:"Blue", code: "#0000FF"]).save(update:false, failOnError:true)
                new Color([name:"MediumBlue", code: "#0000CD"]).save(update:false, failOnError:true)
                new Color([name:"DarkBlue", code: "#00008B"]).save(update:false, failOnError:true)
                new Color([name:"Navy", code: "#000080"]).save(update:false, failOnError:true)
                new Color([name:"MidnightBlue", code: "#191970"]).save(update:false, failOnError:true)
                new Color([name:"Cornsilk", code: "#FFF8DC"]).save(update:false, failOnError:true)
                new Color([name:"BlanchedAlmond", code: "#FFEBCD"]).save(update:false, failOnError:true)
                new Color([name:"Bisque", code: "#FFE4C4"]).save(update:false, failOnError:true)
                new Color([name:"NavajoWhite", code: "#FFDEAD"]).save(update:false, failOnError:true)
                new Color([name:"Wheat", code: "#F5DEB3"]).save(update:false, failOnError:true)
                new Color([name:"BurlyWood", code: "#DEB887"]).save(update:false, failOnError:true)
                new Color([name:"Tan", code: "#D2B48C"]).save(update:false, failOnError:true)
                new Color([name:"RosyBrown", code: "#BC8F8F"]).save(update:false, failOnError:true)
                new Color([name:"SandyBrown", code: "#F4A460"]).save(update:false, failOnError:true)
                new Color([name:"Goldenrod", code: "#DAA520"]).save(update:false, failOnError:true)
                new Color([name:"DarkGoldenrod", code: "#B8860B"]).save(update:false, failOnError:true)
                new Color([name:"Peru", code: "#CD853F"]).save(update:false, failOnError:true)
                new Color([name:"Chocolate", code: "#D2691E"]).save(update:false, failOnError:true)
                new Color([name:"SaddleBrown", code: "#8B4513"]).save(update:false, failOnError:true)
                new Color([name:"Sienna", code: "#A0522D"]).save(update:false, failOnError:true)
                new Color([name:"Brown", code: "#A52A2A"]).save(update:false, failOnError:true)
                new Color([name:"Maroon", code: "#800000"]).save(update:false, failOnError:true)
                new Color([name:"White", code: "#FFFFFF"]).save(update:false, failOnError:true)
                new Color([name:"Snow", code: "#FFFAFA"]).save(update:false, failOnError:true)
                new Color([name:"HoneyDew", code: "#F0FFF0"]).save(update:false, failOnError:true)
                new Color([name:"MintCream", code: "#F5FFFA"]).save(update:false, failOnError:true)
                new Color([name:"Azure", code: "#F0FFFF"]).save(update:false, failOnError:true)
                new Color([name:"AliceBlue", code: "#F0F8FF"]).save(update:false, failOnError:true)
                new Color([name:"GhostWhite", code: "#F8F8FF"]).save(update:false, failOnError:true)
                new Color([name:"WhiteSmoke", code: "#F5F5F5"]).save(update:false, failOnError:true)
                new Color([name:"SeaShell", code: "#FFF5EE"]).save(update:false, failOnError:true)
                new Color([name:"Beige", code: "#F5F5DC"]).save(update:false, failOnError:true)
                new Color([name:"OldLace", code: "#FDF5E6"]).save(update:false, failOnError:true)
                new Color([name:"FloralWhite", code: "#FFFAF0"]).save(update:false, failOnError:true)
                new Color([name:"Ivory", code: "#FFFFF0"]).save(update:false, failOnError:true)
                new Color([name:"AntiqueWhite", code: "#FAEBD7"]).save(update:false, failOnError:true)
                new Color([name:"Linen", code: "#FAF0E6"]).save(update:false, failOnError:true)
                new Color([name:"LavenderBlush", code: "#FFF0F5"]).save(update:false, failOnError:true)
                new Color([name:"MistyRose", code: "#FFE4E1"]).save(update:false, failOnError:true)
                new Color([name:"Gainsboro", code: "#DCDCDC"]).save(update:false, failOnError:true)
                new Color([name:"LightGray", code: "#D3D3D3"]).save(update:false, failOnError:true)
                new Color([name:"Silver", code: "#C0C0C0"]).save(update:false, failOnError:true)
                new Color([name:"DarkGray", code: "#A9A9A9"]).save(update:false, failOnError:true)
                new Color([name:"Gray", code: "#808080"]).save(update:false, failOnError:true)
                new Color([name:"DimGray", code: "#696969"]).save(update:false, failOnError:true)
                new Color([name:"LightSlateGray", code: "#778899"]).save(update:false, failOnError:true)
                new Color([name:"SlateGray", code: "#708090"]).save(update:false, failOnError:true)
                new Color([name:"DarkSlateGray", code: "#2F4F4F"]).save(update:false, failOnError:true)
                new Color([name:"Black", code: "#000000"]).save(update:false, failOnError:true)
            }
        }
        User.withTransaction { TransactionStatus status ->
            if (User.list().isEmpty()) {
                Organization.withNewTransaction { status1 ->
                    Date now = new Date()
                    Organization org = new Organization(uuid: Organization.COVERITAS_UUID, name: "CoVeritas", created: now, lastUpdated: now).save(failOnError: true)
                    Role adminRole = new Role(name: Role.ADMIN, organization: org).save(failOnError: true)
                    new Role(name: Role.USER, organization: org).save(failOnError: true)
                    User.create(User.SYS_ADMIN_UUID, "admin", org, "@dm1n", [adminRole] as Set<Role>, adminColor?:Color.findByName("FireBrick"))
                }
            }
            if (Policy.list().isEmpty()) {
                Role.findAllByName(Role.ADMIN).each {Role r ->
                    User u = r.users[0]
                    r.grandPermission(Policy.Permission.ADMIN, u.organization)
                }
            }
            ApplicationContext ctx = Holders.grailsApplication.mainContext
            DataSource  ds = ctx.getBean(DataSource)
            Connection c = null
            try {
                c = ds.connection
                c.createStatement().executeUpdate("""alter table ma_view_object alter column viewuuid drop not null;""")
//                if (Policy.all.size()==1) {
//                    ResultSet rs = c.createStatement().executeQuery("""select user_id, project_id from ma_project_users""")
//                    Map<Project,Set<User>> pu = [:]
//                    while (rs.next()) {
//                        Project p = Project.get(rs.getLong("project_id"))
//                        Set<User> users = pu.computeIfAbsent(p, {Project p1-> new LinkedHashSet<User>()})
//                        users.add(User.get(rs.getLong("user_id")))
//                    }
//                    for (Project p in pu.keySet()){
//                        for (User u in pu.get(p)) {
//                            p.addUser(u)
//                            for (View v in p.getViews()) {
//                                v.addUser(u)
//                            }
//                        }
//                    }
//                } else {
//                    c.createStatement().executeUpdate("""drop table if exists ma_project_users cascade;""")
//                }
            } finally {
                if (c!=null) {
                    c.close()
                }
            }

//                ApiService apiService = ctx.getBean(ApiService)
//                apiService.activateAllViews()
//                Project.all.each { Project project ->
//                    if (project.users.isEmpty()) {
//                        project.users = User.findAllByOrganization(project.organization)
//                    }
//                }
//                BigInteger h = 0x00E9263726E15A04F4E2E26E96BFBECFD21EEB343E12E7F4CA8E7E1DA87B7583EC8E464B888D2CA630E5B8451AC14BDB7423879135CFC51FE471FE43101FA60EC7
//                User a = User.get(1)
//                BigInteger h2 = new BigInteger(a.passwordHash)
//                if (h != h2) {
//                    a.changePassword("@dm1n")
//                    a.save([flush:true, update:true])
//                    ApplicationContext ctx = Holders.grailsApplication.mainContext
//                    DataSource  ds = ctx.getBean(DataSource)
//                    Connection c = null
//                    try {
//                        c = ds.connection
//                        c.createStatement().executeUpdate(
//                                "UPDATE ma_user set password_hash = decode('" + Hex.encodeHexString(a.passwordHash) + "', 'hex')," +
//                                        " salt = decode('" + Hex.encodeHexString(a.salt) + "', 'hex')  where id = 1"
//                        )
//                    } finally {
//                        if (c!=null) {
//                            c.close()
//                        }
//                    }
//                }
        }
    }
    def destroy = {
    }
}

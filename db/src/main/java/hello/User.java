package hello;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "u_lvl")
    private Integer u_lvl;

    @Column(name = "time")
    private Integer time;

    @Column(name = "login")
    private Integer login;

    @Column(name = "nfc")
    private String nfc;

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public Integer getLogin() {
		 return login;
	}

	public void setLogin(Integer login) {
	         this.login = login;
	}		

	public String getNfc() {
		return nfc;
	}

	public void setNfc(String nfc) {
		this.nfc = nfc;
	}

	public Integer getU_lvl() {
		return u_lvl;
	}

	public void setU_lvl(Integer u_lvl) {
		this.u_lvl = u_lvl;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "name='" + name +", u_lvl=" + u_lvl ;
	}
}


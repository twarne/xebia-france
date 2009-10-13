package fr.xebia.coherence.persistence.sqlmap;

import java.sql.SQLException;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapExecutor;

import fr.xebia.coherence.bean.Commune;
import fr.xebia.coherence.persistence.CatalogDao;

public class SqlMapCatalogDao extends SqlMapClientDaoSupport implements CatalogDao {

	public Commune getCommuneById(final Integer id) {
		return (Commune)getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                return executor.queryForObject("getCommuneById",id);
            }
        });

	}

	@SuppressWarnings("unchecked")
	public List<Commune> getCommuneByName(final String name) {
		return (List<Commune> )getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                return executor.queryForList("getCommuneByName",name);
            }
        });
	}

	@SuppressWarnings("unchecked")
	public List<Commune> getAllCommunes() {
		return (List<Commune> )getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                return executor.queryForList("getAllCommunes");
            }
        });
	}

	
}

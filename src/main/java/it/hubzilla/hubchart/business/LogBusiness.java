package it.hubzilla.hubchart.business;

import it.hubzilla.hubchart.AppConstants;
import it.hubzilla.hubchart.OrmException;
import it.hubzilla.hubchart.model.Logs;
import it.hubzilla.hubchart.persistence.HibernateSessionFactory;
import it.hubzilla.hubchart.persistence.LogsDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class LogBusiness {

	public static List<Logs> findLogs() throws OrmException {
		List<Logs> result = new ArrayList<Logs>();
		Session ses = HibernateSessionFactory.getSession();
		try {
			List<Logs> list = new LogsDao().findLogs(ses);
			if (list != null) result = list;
		} catch (OrmException e) {
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}
	
	public static void addLog(String level, String service, String message) throws OrmException {
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			new LogsDao().addLog(ses, level, service, message);
			trn.commit();
		} catch (OrmException e) {
			trn.rollback();
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	public void deleteOldLogs() throws OrmException {
		Session ses = HibernateSessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, (-1)*AppConstants.LOG_EXPIRATION_DAYS);
		Date fromDate = cal.getTime();
		try {
			new LogsDao().deleteLogs(ses, fromDate);
			trn.commit();
		} catch (OrmException e) {
			trn.rollback();
			throw new OrmException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
}

package net.alfss.smsserver.database.dao.impl;

import net.alfss.smsserver.database.dao.MessageDAO;
import net.alfss.smsserver.database.entity.Channel;
import net.alfss.smsserver.database.entity.Message;
import net.alfss.smsserver.database.entity.Status;
import net.alfss.smsserver.database.exceptions.DatabaseError;
import net.alfss.smsserver.utils.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * User: alfss
 * Date: 09.12.13
 * Time: 15:30
 */
public class MessageDAOImpl implements MessageDAO {

    private StatusDAOImpl statusDAO = new StatusDAOImpl();

    @Override
    public Message get(int messageId) {
        Session session = getSession();
        try {
            session.beginTransaction();
            Message message = (Message) session.get(Message.class, messageId);
            session.getTransaction().commit();
            return message;
        } catch (RuntimeException e ) {
            session.getTransaction().rollback();
            throw new DatabaseError(e);
        }
    }

    @Override
    public int create(Message message) {
        Session session = getSession();
        try {
            session.beginTransaction();
            int id = (Integer) session.save(message);
            session.getTransaction().commit();
            return id;
        } catch (RuntimeException e ) {
            session.getTransaction().rollback();
            throw new DatabaseError(e);
        }
    }

    @Override
    public void create(Message message, Channel channel) {
        message.setChannel(channel);
        message.setQueueName(channel.getQueue());
        message.setPayload(channel.isPayload());
        create(message);
    }

    @Override
    public void update(Message message) {
        Session session = getSession();
        try {
            session.beginTransaction();
            session.update(message);
            session.getTransaction().commit();
        } catch (RuntimeException e ) {
            session.getTransaction().rollback();
            throw new DatabaseError(e);
        }
    }

    @Override
    public Message getWaitResponse(int sequenceNumber, Channel channel) {
        Status status = statusDAO.getByName("WAITING_RESPONSE");
        Session session = getSession();
        try {
            session.beginTransaction();
            Criteria queryCriteria = session.createCriteria(Message.class)
                    .add(Restrictions.eq("sequenceNumber", sequenceNumber))
                    .createAlias("channel", "channel")
                    .add(Restrictions.eq("channel.id", channel.getChannelId()))
                    .add(Restrictions.eq("status.id", status.getStatusId()))
                    .addOrder(Order.desc("updateTime"))
                    .setFirstResult(0);
            Message message = (Message) queryCriteria.list().get(0);
            session.getTransaction().commit();
            return  message;
        } catch (RuntimeException e ) {
            session.getTransaction().rollback();
            throw new DatabaseError(e);
        }
    }

    @Override
    public Message getWaiteDelivery(int messageSmsId, String queue) {
        Status status = statusDAO.getByName("WAITING_DELIVERY");
        Session session = getSession();
        try {
            session.beginTransaction();
            Criteria queryCriteria = session.createCriteria(Message.class)
                    .add(Restrictions.eq("messageSmsId", messageSmsId))
                    .add(Restrictions.eq("queueName", queue))
                    .add(Restrictions.eq("status.id", status.getStatusId()))
                    .setFirstResult(0);
            Message message = (Message) queryCriteria.list().get(0);
            session.getTransaction().commit();
            return message;
        } catch (RuntimeException e ) {
            session.getTransaction().rollback();
            throw new DatabaseError(e);
        }
    }

    @Override
    public Message getWaiteDelivery(int messageSmsId, Channel channel) {
        return getWaiteDelivery(messageSmsId, channel.getQueue());
    }

    @Override
    public void setStatusFail(Message message) {
        Status status = statusDAO.getByName("FAIL");
        Session session = getSession();
        try {
            session.beginTransaction();
            message.setStatus(status);
            session.getTransaction().commit();
        } catch (RuntimeException e ) {
            session.getTransaction().rollback();
            throw new DatabaseError(e);
        }
    }

    @Override
    public void setStatusFail(int messageId) {
        Message message = get(messageId);
        setStatusFail(message);
    }

    @Override
    public void setStatusSuccess(Message message) {
        Status status = statusDAO.getByName("SUCCESS");
        Session session = getSession();
        try {
            session.beginTransaction();
            message.setStatus(status);
            session.update(message);
            session.getTransaction().commit();
        } catch (RuntimeException e ) {
            session.getTransaction().rollback();
            throw new DatabaseError(e);
        }
    }

    @Override
    public void setStatusSuccess(int messageId) {
        Message message = get(messageId);
        setStatusSuccess(message);
    }

    @Override
    public void setStatusWaitSend(Message message) {
        Status status = statusDAO.getByName("WAITING_SEND");
        Session session = getSession();
        try {
            session.beginTransaction();
            message.setStatus(status);
            session.update(message);
            session.getTransaction().commit();
        } catch (RuntimeException e ) {
            session.getTransaction().rollback();
            throw new DatabaseError(e);
        }
    }

    @Override
    public void setStatusWaitSend(int messageId) {
        Message message = get(messageId);
        setStatusWaitSend(message);
    }

    @Override
    public void setStatusWaitResponse(Message message) {
        Status status = statusDAO.getByName("WAITING_RESPONSE");
        Session session = getSession();
        try {
            session.beginTransaction();
            message.setStatus(status);
            session.update(message);
            session.getTransaction().commit();
        } catch (RuntimeException e ) {
            session.getTransaction().rollback();
            throw new DatabaseError(e);
        }
    }

    @Override
    public void setStatusWaitResponse(int messageId) {
        Message message = get(messageId);
        setStatusWaitResponse(message);
    }

    @Override
    public void setStatusWaiteDelivery(Message message) {
        Status status = statusDAO.getByName("WAITING_DELIVERY");
        Session session = getSession();
        try {
            session.beginTransaction();
            message.setStatus(status);
            session.update(message);
            session.getTransaction().commit();
        } catch (RuntimeException e ) {
            session.getTransaction().rollback();
            throw new DatabaseError(e);
        }

    }


    @Override
    public void setStatusWaiteDelivery(int messageId) {
        Message message = get(messageId);
        setStatusWaiteDelivery(message);
    }

    @Override
    public List getList(String queue, int limit, int offset) {
        Session session = getSession();
        try {
            session.beginTransaction();
            List list = session.createCriteria(Message.class)
//                    .setFirstResult(offset)
//                    .setMaxResults(limit)
//                    .add(Restrictions.eq("queueName", queue))
                    .list();
            session.getTransaction().commit();
            return list;
        } catch (RuntimeException e ) {
            session.getTransaction().rollback();
            throw new DatabaseError(e);
        }
    }

    @Override
    public List getList(Channel channel, int limit, int offset) {
        Session session = getSession();
        try {
            session.beginTransaction();
            Criteria criteria = session.createCriteria(Message.class)
                    .add(Restrictions.eq("channel", channel))
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .addOrder(Order.desc("messageId"));
            List list =  criteria.list();
            session.getTransaction().commit();
            return list;
        } catch (RuntimeException e ) {
            session.getTransaction().rollback();
            throw new DatabaseError(e);
        }
    }

    @Override
    public List getList(String queue, String status, int limit, int offset) {
        Session session = getSession();
        try {
            session.beginTransaction();
            Criteria criteria = session.createCriteria(Message.class)
                    .add(Restrictions.eq("queueName", queue))
                    .add(Restrictions.eq("status", status))
                    .setFirstResult(offset)
                    .setMaxResults(limit);
            List list =  criteria.list();
            session.getTransaction().commit();
            return list;
        } catch (RuntimeException e ) {
            session.getTransaction().rollback();
            throw new DatabaseError(e);
        }
    }

    @Override
    public List getList(Channel channel, String status, int limit, int offset) {
        return getList(channel.getQueue(), status, limit, offset);
    }

    private Session getSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }
}

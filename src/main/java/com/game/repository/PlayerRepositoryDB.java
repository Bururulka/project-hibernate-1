package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.jaxb.mapping.spi.NamedQuery;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
@Component
public class PlayerRepositoryDB implements IPlayerRepository {

    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
//        properties.put(Environment.DRIVER, "org.postgresql.Driver");
//        properties.put(Environment.URL, "jdbc:postgresql://localhost:5433/postgres?currentSchema=rpg");
        properties.put(Environment.USER, "user");
        properties.put(Environment.PASS, "password");
        properties.put(Environment.HBM2DDL_AUTO, "update");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:postgresql://localhost:5433/postgres?currentSchema=rpg");
        this.sessionFactory = new Configuration()
                .addProperties(properties)
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<Player> nQuery = session.createNativeQuery("select * from rpg.player ORDER BY id", Player.class);
            nQuery.setFirstResult(pageNumber * pageSize);
            nQuery.setMaxResults(pageSize);
            return nQuery.list();
        }
    }

    @Override

    public int getAllCount() {
        try(Session session = sessionFactory.openSession()){
            Query<Long> lQuery = session.createNamedQuery("player_getAllCount", Long.class);
            return Math.toIntExact(lQuery.uniqueResult());
        }
    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(player);
            transaction.commit();
            return player;
        }
    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(player);
            transaction.commit();
            return player;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.of(session.find(Player.class, id));
        }

    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(player);
            transaction.commit();
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}
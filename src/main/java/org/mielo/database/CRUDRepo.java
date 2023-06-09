package org.mielo.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
public class CRUDRepo {

    @Getter
    @Setter
    private EntityManager entityManager;

    public <T> List<T> findAll(Class<T> type) {
        return this.entityManager.createQuery("SELECT c FROM " + type.getSimpleName() + " c").getResultList();
    }

    public <T> List<T> findAllByProperty(Class<T> type, Property property) {
        return this.entityManager.createQuery(
                "SELECT c FROM " + type.getSimpleName() + " c where c." + property.name() + " = " + property.valueAsString())
                .getResultList();
    }

    public <ID, T extends IdAwareEntity<ID>> Optional<T> find(Class<T> type, ID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.entityManager.find(type, id));
    }

    public <T> Optional<T> findByProperty(Class<T> type, Property property) {
        List<T> results = entityManager.createQuery("SELECT c FROM " + type.getSimpleName() + " c where c." + property.name() + " = " + property.valueAsString()).getResultList();
        return isEmpty(results) ? Optional.empty() : Optional.of(results.get(0));
    }

    public <T> Optional<T> findByPropertiesWithAnd(Class<T> type, Properties properties) {
        List<T> results = entityManager.createQuery("SELECT c FROM " + type.getSimpleName() + " c where " + properties.whereClause("c")).getResultList();
        return isEmpty(results) ? Optional.empty() : Optional.of(results.get(0));
    }

    public <T> List<T> findAllByPropertiesWithAnd(Class<T> type, Properties properties) {
        return entityManager.createQuery("SELECT c FROM " + type.getSimpleName() + " c where " + properties.whereClause("c")).getResultList();
    }

    public <T extends IdAwareEntity> Optional<T> save(T t) {
        if (t.isPersisted()) {
            return Optional.of(entityManager.merge(t));
        }
        entityManager.persist(t);
        return Optional.of(t);
    }

    public <T> void delete(T t) {
        if (!entityManager.contains(t)) {
            t = entityManager.merge(t);
        }
        this.entityManager.remove(t);
    }

    public <T extends IdAwareEntity<ID>, ID> void deleteById(Class<T> type, ID id) {
        deleteByProperty(type, new Property("id", id));;
    }

    public <T extends IdAwareEntity<ID>, ID> void deleteByProperty(Class<T> type, Property property) {
        String hql = "delete from " + type.getSimpleName() + " where " + property.name() + " = " + property.valueAsString();
        entityManager.createQuery(hql).executeUpdate();
    }

    public <T> List<T> findByNativeQuery(String sql, Properties properties, Class<T> cls) {
        Query query = this.entityManager.createNativeQuery(sql, cls);
        if(properties != null) {
            properties.get().forEach(property -> query.setParameter(property.name(), property.val()));
        }
        return query.getResultList();
    }

    public <T> List<T> findByNativeQuery(final String sql, Properties parameters) {
        Query query = this.entityManager.createNativeQuery(sql);
        Optional.of(parameters).ifPresent(properties -> properties.get().forEach(property -> query.setParameter(property.name(), property.val())));
        return query.getResultList();
    }

    public <T> List<T> findByNativeQuery(String sql, Properties properties, RowMapper<T> rowMapper) {
        List<T> retVal = new ArrayList<>();

        List rows = findByNativeQuery(sql, properties);
        if (rows != null && !rows.isEmpty()) {
            for (Object row : rows) {
                Object[] rowAsArray;
                if (row instanceof Object[]) {
                    rowAsArray = (Object[]) row;
                } else {
                    rowAsArray = new Object[]{row};
                }

                // If for some reason the callback returns
                T afterCallback = rowMapper.mapRow(rowAsArray);
                if (afterCallback != null) {
                    retVal.add(afterCallback);
                }
            }
        }

        return retVal;
    }

    public <T> void detach(T t) {
        entityManager.detach(t);
    }

    private boolean isEmpty(Collection<?> target) {
        return target == null || target.isEmpty();
    }
}

package io.oasp.module.jpa.dataaccess.impl.data;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.querydsl.core.alias.Alias;
import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;

import io.oasp.module.jpa.dataaccess.api.data.GenericRepository;

/**
 * Implementation of {@link GenericRepository} based on {@link SimpleJpaRepository}. All repository interfaces derived
 * from {@link GenericRepository} will be based on this implementation at runtime.
 *
 * @param <E> generic type of the managed {@link #getEntityClass() entity}.
 * @param <ID> generic type of the {@link net.sf.mmm.util.entity.api.PersistenceEntity#getId() primary key} of the
 *        entity.
 *
 * @since 3.0.0
 */
// QueryDslJpaRepository<E, ID> forces you to use QueryDSL APT generation what is not desired
// therefore no support for QueryDslPredicateExecutor (we offer more flexible QueryDSL support anyhow)
public class GenericRepositoryImpl<E, ID extends Serializable> extends SimpleJpaRepository<E, ID>
    implements GenericRepository<E, ID> {

  /** The {@link EntityManager} instance. */
  protected final EntityManager entityManager;

  /** The {@link JpaEntityInformation}. */
  protected final JpaEntityInformation<E, ?> entityInformation;

  /**
   * The constructor.
   *
   * @param entityInformation the {@link JpaEntityInformation}.
   * @param entityManager the JPA {@link EntityManager}.
   */
  public GenericRepositoryImpl(JpaEntityInformation<E, ID> entityInformation, EntityManager entityManager) {

    super(entityInformation, entityManager);
    this.entityManager = entityManager;
    this.entityInformation = entityInformation;
  }

  @Override
  public void forceIncrementModificationCounter(E entity) {

    this.entityManager.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
  }

  @Override
  public Class<E> getEntityClass() {

    return this.entityInformation.getJavaType();
  }

  @Override
  public JPAQuery<E> newDslQuery() {

    return new JPAQuery<>(this.entityManager);
  }

  @Override
  public JPAQuery<E> newDslQuery(E alias) {

    return newDslQuery().from(Alias.$(alias));
  }

  @Override
  public JPADeleteClause newDslDeleteClause(E alias) {

    return new JPADeleteClause(this.entityManager, Alias.$(alias));
  }

  @Override
  public JPADeleteClause newDslDeleteClause(EntityPath<E> entityPath) {

    return new JPADeleteClause(this.entityManager, entityPath);
  }

  @Override
  public E newDslAlias() {

    return Alias.alias(this.entityInformation.getJavaType());
  }

}

package com.erdouglass.emdb.media.entity;

import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import com.erdouglass.emdb.common.CreditType;

@Entity
@Table(name = "Credits")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(
    name = BasicEntity.SEQUENCE_GENERATOR, 
    sequenceName = "credit_sequence", 
    initialValue = 1,
    allocationSize = 50)
public class Credit extends BasicEntity<String> {
  private static final int CREDIT_TYPE_MAX_LENGTH = 4;
  
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "credit_type", updatable = false, length = CREDIT_TYPE_MAX_LENGTH)
  private CreditType type;
  
  @PositiveOrZero
  @Column(name = "credit_order")
  private Integer order;
  
  /// The @JoinColumn annotation maps the {@link Person#id} primary key to the
  /// foreign key in the Credits table. A {@code Credit} can't exist without a 
  /// {@link Movie} or {@link Series}.
  @ManyToOne
  @JoinColumn(name = "person_id", updatable = false, nullable = false)
  private Person person;
  
  Credit() {

  }
  
  public Credit(String tmdbId) {
    super(tmdbId);
  }
  
  public void type(CreditType type) {
    this.type = type;
  }
  
  public CreditType type() {
    return type;
  }
  
  public void order(Integer order) {
    this.order = order;
  }
  
  public Optional<Integer> order() {
    return Optional.ofNullable(order);
  }
  
  public void person(Person person) {
    this.person = person;
  }
  
  public Person person() {
    return person;
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id()
      + ", type=" + type()
      + ", order=" + order().orElse(null)
      + "]";
  }

}

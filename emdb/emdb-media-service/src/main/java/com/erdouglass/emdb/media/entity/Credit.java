package com.erdouglass.emdb.media.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import com.erdouglass.emdb.common.query.CreditType;

@Entity
@Table(name = "Credits")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Credit extends UuidEntity {
  private static final int CREDIT_TYPE_MAX_LENGTH = 4;
  
  @PositiveOrZero
  @Column(name = "credit_order")
  private Integer order;  
  
  /// The @JoinColumn annotation maps the {@link Person#id} primary key to the
  /// foreign key in the Credits table. A {@code Credit} can't exist without a 
  /// {@link Movie} or {@link Series}.
  @ManyToOne
  @JoinColumn(name = "person_id", updatable = false, nullable = false)
  private Person person; 
  
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "credit_type", updatable = false, length = CREDIT_TYPE_MAX_LENGTH)
  private CreditType type;  
  
  public Credit() {
    
  }
  
  public void setOrder(Integer order) {
    this.order = order;
  }
  
  public Integer getOrder() {
    return order;
  } 
  
  public void setPerson(Person person) {
    this.person = person;
  }
  
  public Person getPerson() {
    return person;
  }
  
  public void setType(CreditType type) {
    this.type = type;
  }
  
  public CreditType getType() {
    return type;
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + getId()
      + ", type=" + getType()
      + ", order=" + getOrder()
      + "]";
  }    

}

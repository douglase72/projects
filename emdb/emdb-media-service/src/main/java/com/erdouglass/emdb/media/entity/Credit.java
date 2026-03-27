package com.erdouglass.emdb.media.entity;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@Entity
@Table(name = "Credits")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Credit {
  private static final int CREDIT_TYPE_MAX_LENGTH = 4;
  
  public enum CreditType {
    CAST("cast"), 
    CREW("crew");
    
    private static final Map<String, CreditType> CACHE = Stream.of(values())
        .collect(Collectors.toMap(CreditType::name, Function.identity()));
    
    private final String type;
    
    CreditType(String type) {
      this.type = type;
    }
    
    @JsonCreator
    public static CreditType from(String type) {
      return Optional.ofNullable(CACHE.get(type.toUpperCase()))
          .orElseThrow(() -> new IllegalArgumentException("Invalid credit type: " + type));
    }
     
    @Override
    @JsonValue
    public String toString() {
      return type;
    }
  }
  
  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Instant created;
  
  @Id
  @GeneratedValue
  @UuidGenerator(style = UuidGenerator.Style.TIME)
  private UUID id;
  
  @UpdateTimestamp
  @Column(nullable = false)
  private Instant modified;
  
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
  
  public Instant getCreated() {
    return created;
  }

  public UUID getId() {
    return id;
  }
  
  public Instant getModified() {
    return modified;
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

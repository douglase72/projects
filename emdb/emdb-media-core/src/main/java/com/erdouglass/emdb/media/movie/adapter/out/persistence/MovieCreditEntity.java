package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.erdouglass.emdb.media.kernel.SourceId.Source;
import com.erdouglass.emdb.media.movie.domain.Movie;
import com.erdouglass.emdb.media.movie.domain.Role;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "movie_credit",
       uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "source", "source_id"}))
class MovieCreditEntity {
  
  @Enumerated(EnumType.STRING)
  @Column(name = "credit_type", nullable = false, length = 8)
  private CreditType creditType;
  
  private String department;
  
  @Id
  private UUID id;
  
  /// The @JoinColumn annotation maps the {@link Movie#id} primary key to the 
  /// foreign key in the Credits table. A {@code MovieCredit} can't exist 
  /// without a {@link Movie}.
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "movie_id", nullable = false)
  private MovieEntity movie;
  
  @Column(name = "name", nullable = false)
  private String name;
  
  @Column(name = "credit_order")
  private Integer order;
  
  @Column(name = "source_person_id", nullable = false, length = 64)
  private String personId;
  
  @Column(length = Role.MAX_LENGTH)
  private String role;
  
  @Enumerated(EnumType.STRING)
  @Column(length = Source.MAX_LENGTH)
  private Source source;
  
  @Column(name = "source_id", nullable = false, length = 64)
  private String sourceId;
  
  MovieCreditEntity() {}
  
  public enum CreditType { CAST, CREW }
}

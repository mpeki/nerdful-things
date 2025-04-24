package dk.pekilidi.db;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;

@Getter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@NamedNativeQuery(
        name = "NamedNativeQueryEntity.test",
        query = "select 1 as id",
        resultClass = NamedNativeQueryEntity.class,
        readOnly = true
)
public abstract class NamedNativeQueryEntity {

    public static final String DEFAULT_QUERY_NAME = "NamedNativeQueryEntity.test";

    @Id
    private Long id;

    @Column(name = "created_at")
    protected Instant createdAt;

    @Column(name = "updated_at")
    protected Instant lastModifiedAt;

    private static Class<?> getActualClass(Object entity) {
        if (entity instanceof HibernateProxy proxy) {
            return proxy.getHibernateLazyInitializer().getPersistentClass();
        }
        return entity.getClass();
    }

    /**
     * Implements the logic as described here:
     * - <a href="https://jpa-buddy.com/blog/hopefully-the-final-article-about-equals-and-hashcode-for-jpa-entities-with-db-generated-ids/">hopefully-the-final-article-about-equals-and-hashcode-for-jpa-entities-with-db-generated-ids</a>
     * - <a href="https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/">how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier</a>
     * - <a href="https://stackoverflow.com/questions/77052414/yet-another-implementation-of-jpa-entity-equals-hashcode">yet-another-implementation-of-jpa-entity-equals-hashcode</a>
     *
     * @param o
     * @return
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }

        if (getActualClass(this) != getActualClass(o)) return false;
        NamedNativeQueryEntity that = (NamedNativeQueryEntity) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public final int hashCode() {
        return getActualClass(this).hashCode();
    }

}

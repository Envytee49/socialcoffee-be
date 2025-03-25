package com.example.socialcoffee.repository.custom.impl;//package com.example.socialcoffee.repository.custom.impl;
//
//import com.example.socialcoffee.model.feature.Feature;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.Query;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public class FeatureCustomRepositoryImpl implements FeatureCustomRepository {
//    @PersistenceContext
//    private EntityManager em;
//    @Override
//    public <T extends Feature> T findByName(final String name,
//                                            final Class<T> tClass) {
//        String sql = """
//                SELECT f FROM Feature f WHERE f.name=:name AND TYPE(f) = :tClass
//                """;
//        final Query query = em.createQuery(sql, Feature.class).setParameter("name",
//                                                             name).setParameter("tClass",
//                                                                                tClass);
//        return query.getSingleResult();
//    }
//}

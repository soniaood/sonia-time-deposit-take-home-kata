package org.ikigaidigital.dto;

import java.util.Collection;

public record ListResponse<T>(Collection<T> data) {

    public static <T> ListResponseBuilder<T> data(Collection<T> data) {
        return new ListResponseBuilder<>(data);
    }

    public static final class ListResponseBuilder<T> {
        private Collection<T> data;

        private ListResponseBuilder(Collection<T> data) {
            this.data = data;
        }

        public ListResponseBuilder<T> withData(Collection<T> data) {
            this.data = data;
            return this;
        }

        public ListResponse<T> build() {
            return new ListResponse<>(data);
        }
    }
}

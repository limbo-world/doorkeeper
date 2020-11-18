/*
 *
 *  *  Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package org.limbo.doorkeeper.server.utils;


import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Brozen
 * @date 2019/7/4 11:09 AM
 * @email brozen@qq.com
 */
public class EnhancedCollectionUtils {


    public static <E, K> Map<K, E> map(Collection<E> collection, Function<E, K> keyGenerator) {
        return map(collection, keyGenerator, input -> input);
    }

    public static <E, K, V> Map<K, V> map(Collection<E> collection, Function<E, K> keyGenerator,
                                          Function<E, V> valueGenerator) {
        return map(collection, keyGenerator, valueGenerator, (Supplier<Map<K, V>>) HashMap::new);
    }

    public static <E, K, V, M extends Map<K, V>> Map<K, V> map(Collection<E> collection, Function<E, K> keyGenerator,
                                                               Function<E, V> valueGenerator, Supplier<M> collectionSupplier) {
        Objects.requireNonNull(collection);
        Objects.requireNonNull(keyGenerator);
        Objects.requireNonNull(valueGenerator);
        Objects.requireNonNull(collectionSupplier);

        M resultMap = collectionSupplier.get();
        for (E e : collection) {
            K key = keyGenerator.apply(e);
            V value = valueGenerator.apply(e);
            resultMap.put(key, value);
        }

        return resultMap;
    }

    public static <T, E> Map<T, Collection<E>> group(Collection<E> collection, Function<E, T> keyGenerator) {
        return group(collection, keyGenerator, () -> new LinkedList<>());
    }

    public static <T, E, C extends Collection<E>> Map<T, C> group(Collection<E> collection, Function<E, T> keyGenerator, Supplier<C> collectionSupplier) {
        Objects.requireNonNull(keyGenerator);
        Objects.requireNonNull(collectionSupplier);

        Map<T, C> groupedResult = new HashMap<>();
        for (E e : collection) {
            T key = keyGenerator.apply(e);
            if (!groupedResult.containsKey(key)) {
                C coll = collectionSupplier.get();
                groupedResult.put(key, coll);
            }

            groupedResult.get(key).add(e);
        }

        return groupedResult;
    }

    public static <I, O> Collection<O> transform(final Collection<I> input, Function<I, O> transformer) {
        return transform(input, transformer, (Supplier<Collection<O>>) () -> new ArrayList<>(input.size()));
    }

    public static <I, O, C extends Collection<O>> C transform(Collection<I> input, Function<I, O> transformer, Supplier<C> collectionSupplier) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(transformer);
        Objects.requireNonNull(collectionSupplier);

        C result = collectionSupplier.get();
        for (I i : input) {
            result.add(transformer.apply(i));
        }
        return result;
    }

    public static <E, K extends Comparable<K>> Collection<E> distinct(Collection<E> input, Function<E, K> idGenerator) {
        return distinct(input, idGenerator, () -> new ArrayList<>(input.size()));
    }

    public static <E, K extends Comparable<K>, C extends Collection<E>>
            C distinct(Collection<E> input, Function<E, K> idGenerator, Supplier<C> collectionSupplier) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(idGenerator);
        Objects.requireNonNull(collectionSupplier);

        Set<K> idSet = new HashSet<>();
        C result = collectionSupplier.get();
        Objects.requireNonNull(result, "collectionSupplier return a null collection!");

        for (E e : input) {
            K id = idGenerator.apply(e);
            if (!idSet.contains(id)) {
                idSet.add(id);
                result.add(e);
            }
        }

        return result;
    }

}

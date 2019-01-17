/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.internal.collection;

import java.util.Comparator;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class CharStoreTest {

	@Test
	public void sort() {
		final char[]  array = "weoriAFQErqqöp".toCharArray();
		final CharStore store = CharStore.of(array);
		store.sort(0, array.length, null);

		Assert.assertEquals(new String(array), "AEFQeiopqqrrwö");
	}

	@Test
	public void sortWithComparator() {
		final char[]  array = "weoriAFQErqqöp".toCharArray();
		final CharStore store = CharStore.of(array);
		store.sort(0, array.length, Comparator.reverseOrder());

		Assert.assertEquals(new String(array), "öwrrqqpoieQFEA");
	}

}

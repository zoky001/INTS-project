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
package io.jenetics.engine;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Executor;

import io.jenetics.Gene;
import io.jenetics.Phenotype;
import io.jenetics.internal.util.Concurrency;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

/**
 * Default phenotype evaluation strategy. It uses the configured {@link Executor}
 * for the fitness evaluation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 4.2
 * @since 4.2
 */
final class ConcurrentEvaluator<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Engine.Evaluator<G, C>
{

	private final Executor _executor;

	ConcurrentEvaluator(final Executor executor) {
		_executor = requireNonNull(executor);
	}

	@Override
	public ISeq<Phenotype<G, C>> evaluate(final Seq<Phenotype<G, C>> population) {
		final ISeq<Phenotype<G, C>> phenotypes = population.stream()
			.filter(pt -> !pt.isEvaluated())
			.collect(ISeq.toISeq());

		if (phenotypes.nonEmpty()) {
			try (Concurrency c = Concurrency.with(_executor)) {
				c.execute(phenotypes);
			}
		}

		return population.asISeq();
	}
}

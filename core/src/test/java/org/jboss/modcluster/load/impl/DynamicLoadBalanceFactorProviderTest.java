/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.modcluster.load.impl;

import org.jboss.modcluster.container.Engine;
import org.jboss.modcluster.load.metric.LoadMetric;
import org.jboss.modcluster.load.metric.NodeUnavailableException;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.when;

/**
 * @author Radoslav Husar
 */
public class DynamicLoadBalanceFactorProviderTest {

    @Test
    public void getLoadBalanceFactor() throws Exception {
        LoadMetric metric = mock(LoadMetric.class);
        Engine engine = mock(Engine.class);

        when(metric.getWeight()).thenReturn(1);
        when(metric.getLoad(same(engine))).thenThrow(new NodeUnavailableException());

        DynamicLoadBalanceFactorProvider provider = new DynamicLoadBalanceFactorProvider(Collections.singleton(metric));

        int loadBalanceFactor = provider.getLoadBalanceFactor(engine);
        assertEquals(-1, loadBalanceFactor);
    }
}

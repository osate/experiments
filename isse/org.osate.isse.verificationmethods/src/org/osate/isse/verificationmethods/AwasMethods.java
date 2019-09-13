/**
 * Copyright (c) 2004-2017 Carnegie Mellon University and others.
 * (see Contributors file).
 *
 * All Rights Reserved.
 *
 * NO WARRANTY. ALL MATERIAL IS FURNISHED ON AN "AS-IS" BASIS. CARNEGIE MELLON
 * UNIVERSITY MAKES NO WARRANTIES OF ANY KIND, EITHER EXPRESSED OR IMPLIED, AS
 * TO ANY MATTER INCLUDING, BUT NOT LIMITED TO, WARRANTY OF FITNESS FOR PURPOSE
 * OR MERCHANTABILITY, EXCLUSIVITY, OR RESULTS OBTAINED FROM USE OF THE
 * MATERIAL. CARNEGIE MELLON UNIVERSITY DOES NOT MAKE ANY WARRANTY OF ANY KIND
 * WITH RESPECT TO FREEDOM FROM PATENT, TRADEMARK, OR COPYRIGHT INFRINGEMENT.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Created, in part, with funding and support from the United States Government.
 * (see Acknowledgments file).
 *
 * This program includes and/or can make use of certain third party source code,
 * object code, documentation and other files ("Third Party Software"). The
 * Third Party Software that is used by this program is dependent upon your
 * system configuration. By using this program, You agree to comply with any and
 * all relevant Third Party Software terms and conditions contained in any such
 * Third Party Software or separate license file distributed with such Third
 * Party Software. The parties who own the Third Party Software ("Third Party
 * Licensors") are intended third party beneficiaries to this License with
 * respect to the terms applicable to their Third Party Software. Third Party
 * Software licenses only apply to the Third Party Software and not any other
 * portion of this program or this program as a whole.
 */
package org.osate.isse.verificationmethods;

import java.util.Map;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osate.aadl2.instance.ComponentInstance;
import org.osgi.framework.Bundle;
import org.sireum.aadl.osate.util.Util;
import org.sireum.awas.AADLBridge.AadlHandler;
import org.sireum.awas.ast.Model;
import org.sireum.awas.awasfacade.AwasGraphImpl;
import org.sireum.awas.awasfacade.Collector;
import org.sireum.awas.fptc.FlowEdge;
import org.sireum.awas.fptc.FlowGraph;
import org.sireum.awas.fptc.FlowNode;
import org.sireum.awas.symbol.SymbolTable;
import org.sireum.util.ConsoleTagReporter;

public class AwasMethods {
	private static final String BUNDLE_NAME;
	private static final ILog LOG;

	static {
		Bundle bundle = Activator.getContext().getBundle();
		BUNDLE_NAME = bundle.getSymbolicName();
		LOG = Platform.getLog(bundle);
	}

	public boolean isQueryEmpty(ComponentInstance component, String query) throws Throwable {
		try {
			Model awasModel = AadlHandler.buildAwasModel(Util.getAir(component));
			SymbolTable symbolTable = SymbolTable.apply(awasModel, new ConsoleTagReporter());
			FlowGraph<FlowNode, FlowEdge<FlowNode>> flowGraph = FlowGraph.apply(awasModel, symbolTable, false);

			Map<String, Collector> queryRes = new AwasGraphImpl(flowGraph, symbolTable).queryEvaluator(query);
			Boolean res = queryRes.entrySet().stream().allMatch(p -> p.getValue().getGraph().isEmpty());
			return res;
		} catch (Throwable e) {
			LOG.log(new Status(IStatus.ERROR, BUNDLE_NAME, "Awas exception for query: " + query, e));
			throw e;
		}
	}

	public boolean isQueryNotEmpty(ComponentInstance component, String query) throws Throwable {
		return !isQueryEmpty(component, query);
	}
}
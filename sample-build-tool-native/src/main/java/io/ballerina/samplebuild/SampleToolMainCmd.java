/*
 *  Copyright (c) 2024, WSO2 LLC. (https://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.samplebuild;

import io.ballerina.projects.buildtools.CodeGeneratorTool;
import io.ballerina.projects.buildtools.ToolConfig;
import io.ballerina.projects.buildtools.ToolContext;
import io.ballerina.tools.diagnostics.DiagnosticFactory;
import io.ballerina.tools.diagnostics.DiagnosticInfo;
import io.ballerina.tools.diagnostics.DiagnosticSeverity;
import io.ballerina.tools.diagnostics.Location;
import io.ballerina.tools.text.LinePosition;
import io.ballerina.tools.text.LineRange;
import io.ballerina.tools.text.TextRange;

import java.nio.file.Path;

@ToolConfig(name = "sample_cmd", subcommands = {SubCmdA.class, SubCmdB.class})
public class SampleToolMainCmd implements CodeGeneratorTool {

    @Override
    public void execute(ToolContext toolContext) {
        Path absFilePath = toolContext.currentPackage().project().sourceRoot().resolve(toolContext.filePath());
        // Report diagnostics
        if (!absFilePath.toFile().exists()) {
            DiagnosticInfo diagnosticInfo = new DiagnosticInfo("001",
                    "The provided filePath does not exist", DiagnosticSeverity.ERROR);
            // Report the diagnostic. This will be printed to the console after the execution of the tool.
            // If the diagnostic is an error, it will result in a build failure.
            toolContext.reportDiagnostic(DiagnosticFactory.createDiagnostic(diagnosticInfo, new NullLocation()));
        }

        // Access optional configurations and there locations
        ToolContext.Option modeOption = toolContext.options().get("mode");
        if (!modeOption.value().toString().equals("client")) {
            DiagnosticInfo modeDiagnostic = new DiagnosticInfo("002", "Modes that are not client " +
                    "are not supported", DiagnosticSeverity.WARNING);
            toolContext.reportDiagnostic(DiagnosticFactory.createDiagnostic(modeDiagnostic, modeOption.location()));
        }

        // toolContext.println will immediately print a message to the console.
        toolContext.println("Running sample build tool: " + toolContext.toolId());
        toolContext.println("Cache created at: " + toolContext.cachePath());
        toolContext.println("Output to: " + toolContext.outputPath());
    }

    private static class NullLocation implements Location {

        @Override
        public LineRange lineRange() {
            LinePosition from = LinePosition.from(0, 0);
            return LineRange.from("openAPI sample build tool", from, from);
        }

        @Override
        public TextRange textRange() {
            return TextRange.from(0, 0);
        }
    }
}

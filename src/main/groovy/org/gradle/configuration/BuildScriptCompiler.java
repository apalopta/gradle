/*
 * Copyright 2009 the original author or authors.
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
 */
package org.gradle.configuration;

import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.project.ProjectScript;
import org.gradle.api.internal.project.ImportsReader;
import org.gradle.api.internal.artifacts.dsl.TaskDefinitionScriptTransformer;
import org.gradle.groovy.scripts.*;
import groovy.lang.Script;

public class BuildScriptCompiler implements ProjectEvaluator {
    private final ImportsReader importsReader;
    private final IScriptProcessor scriptProcessor;
    private final IProjectScriptMetaData projectScriptMetaData;

    public BuildScriptCompiler(ImportsReader importsReader, IScriptProcessor scriptProcessor,
                               IProjectScriptMetaData projectScriptMetaData) {
        this.importsReader = importsReader;
        this.scriptProcessor = scriptProcessor;
        this.projectScriptMetaData = projectScriptMetaData;
    }

    public void evaluate(ProjectInternal project) {
        ScriptSource source = new ImportsScriptSource(project.getBuildScriptSource(), importsReader,
                project.getRootDir());
        ScriptProcessor processor = scriptProcessor.createProcessor(source);
        processor.setClassloader(project.getBuildScriptClassLoader());
        processor.setTransformer(new TaskDefinitionScriptTransformer());
        Script buildScript = processor.process(ProjectScript.class);
        projectScriptMetaData.applyMetaData(buildScript, project);
        project.setBuildScript(buildScript);
    }
}

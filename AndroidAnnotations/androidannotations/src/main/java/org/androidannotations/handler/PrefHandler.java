/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.handler;

import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.holder.GeneratedClassHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;

public class PrefHandler extends BaseAnnotationHandler<EComponentHolder> {

	public PrefHandler(ProcessingEnvironment processingEnvironment) {
		super(Pref.class, processingEnvironment);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.isNotPrivate(element, valid);

		validatorHelper.isSharedPreference(element, validatedElements, valid);
	}

	@Override
	public void process(Element element, EComponentHolder holder) {

		String fieldName = element.getSimpleName().toString();
		TypeMirror fieldTypeMirror = element.asType();
		JClass prefClass = refClass(fieldTypeMirror.toString());

		String elementTypeName = fieldTypeMirror.toString();
		int index = elementTypeName.lastIndexOf(".");
		if (index != -1) {
			elementTypeName = elementTypeName.substring(index + 1);
		}

		Set<? extends Element> sharedPrefElements = validatedModel.getRootAnnotatedElements(SharedPref.class.getName());
		for (Element sharedPrefElement : sharedPrefElements) {
			GeneratedClassHolder sharedPrefHolder = processHolder.getGeneratedClassHolder(sharedPrefElement);
			String sharedPrefName = sharedPrefHolder.getGeneratedClass().name();

			if (elementTypeName.equals(sharedPrefName)) {
				prefClass = sharedPrefHolder.getGeneratedClass();
				break;
			}
		}

		JBlock methodBody = holder.getInitBody();
		JFieldRef field = JExpr.ref(fieldName);
		methodBody.assign(field, JExpr._new(prefClass).arg(holder.getContextRef()));
	}
}

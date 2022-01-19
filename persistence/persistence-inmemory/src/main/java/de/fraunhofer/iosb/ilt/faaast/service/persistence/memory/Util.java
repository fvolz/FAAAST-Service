/*
 * Copyright (c) 2021 Fraunhofer IOSB, eine rechtlich nicht selbstaendige
 * Einrichtung der Fraunhofer-Gesellschaft zur Foerderung der angewandten
 * Forschung e.V.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fraunhofer.iosb.ilt.faaast.service.persistence.memory;

import de.fraunhofer.iosb.ilt.faaast.service.model.QueryModifier;
import de.fraunhofer.iosb.ilt.faaast.service.model.v3.api.Extend;
import de.fraunhofer.iosb.ilt.faaast.service.model.v3.api.Level;
import io.adminshell.aas.v3.dataformat.DeserializationException;
import io.adminshell.aas.v3.dataformat.SerializationException;
import io.adminshell.aas.v3.dataformat.core.util.AasUtils;
import io.adminshell.aas.v3.dataformat.json.JsonDeserializer;
import io.adminshell.aas.v3.dataformat.json.JsonSerializer;
import io.adminshell.aas.v3.model.AssetAdministrationShellEnvironment;
import io.adminshell.aas.v3.model.Blob;
import io.adminshell.aas.v3.model.Key;
import io.adminshell.aas.v3.model.KeyElements;
import io.adminshell.aas.v3.model.Operation;
import io.adminshell.aas.v3.model.Referable;
import io.adminshell.aas.v3.model.Reference;
import io.adminshell.aas.v3.model.Submodel;
import io.adminshell.aas.v3.model.SubmodelElement;
import io.adminshell.aas.v3.model.SubmodelElementCollection;
import io.adminshell.aas.v3.model.impl.DefaultKey;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class Util {

    public static <T extends Referable> T deepCopy(Referable referable, Class<T> outputClass) {
        try {
            Referable deepCopy = new JsonDeserializer().readReferable(new JsonSerializer().write(referable), outputClass);
            if (deepCopy.getClass().isAssignableFrom(outputClass)) {
                return (T) deepCopy;
            }
        }
        catch (SerializationException | DeserializationException e) {
            return null;
        }
        return null;
    }


    public static <T extends Referable> List<T> deepCopy(List<T> referableList,
                                                         Class<T> outputClass) {
        List<T> deepCopyList = new ArrayList<>();
        for (Referable referable: referableList) {
            deepCopyList.add(deepCopy(referable, outputClass));
        }
        return deepCopyList;
    }


    public static <T extends Referable> void applyQueryModifier(List<T> referableList, QueryModifier modifier) {
        if (referableList == null) {
            return;
        }
        for (Referable referable: referableList) {
            applyQueryModifier(referable, modifier);
        }
    }


    public static void applyQueryModifier(Referable referable, QueryModifier modifier) {
        if (referable == null || modifier == null) {
            return;
        }
        applyQueryModifierExtend(referable, modifier);
        applyQueryModifierLevel(referable, modifier);
    }


    private static void applyQueryModifierExtend(Referable referable, QueryModifier modifier) {
        Predicate<SubmodelElement> removeFilter = x -> Blob.class.isAssignableFrom(x.getClass());
        if (modifier.getExtend() == Extend.WithoutBLOBValue) {
            if (Submodel.class.isAssignableFrom(referable.getClass())) {
                ((Submodel) referable).getSubmodelElements().removeIf(removeFilter);
            }
            else if (SubmodelElementCollection.class.isAssignableFrom(referable.getClass())) {
                ((SubmodelElementCollection) referable).getValues().removeIf(removeFilter);
            }
        }
    }


    private static void applyQueryModifierLevel(Referable referable, QueryModifier modifier) {
        if (modifier.getLevel() == Level.Deep) {
            //nothing to do here
        }
        else if (modifier.getLevel() == Level.Core) {
            if (Submodel.class.isAssignableFrom(referable.getClass())) {
                ((Submodel) referable).getSubmodelElements().forEach(x -> {
                    if (SubmodelElementCollection.class.isAssignableFrom(x.getClass())) {
                        ((SubmodelElementCollection) x).setValues(null);
                    }
                });
            }
            else if (SubmodelElementCollection.class.isAssignableFrom(referable.getClass())) {
                ((SubmodelElementCollection) referable).getValues().forEach(x -> {
                    if (SubmodelElementCollection.class.isAssignableFrom(x.getClass())) {
                        ((SubmodelElementCollection) x).setValues(null);
                    }
                });
            }
        }
    }


    public static boolean empty(final String s) {
        return s == null || s.trim().isEmpty();
    }


    public static boolean isEqualsIgnoringKeyType(Reference reference, List<Key> keys) {
        if (reference == null || reference.getKeys() == null || keys == null) {
            if (reference == null && keys == null) {
                return true;
            }
            else if (reference != null) {
                return reference.getKeys() == null && keys == null;
            }
            return false;
        }
        List<Key> deepCopiedKeys1 = new ArrayList<>();
        List<Key> deepCopiedKeys2 = new ArrayList<>();
        reference.getKeys().forEach(x -> deepCopiedKeys1.add(deepCopyKeyWithoutKeyElement(x)));
        keys.forEach(x -> deepCopiedKeys2.add(deepCopyKeyWithoutKeyElement(x)));
        return deepCopiedKeys1.containsAll(deepCopiedKeys2) && deepCopiedKeys2.containsAll(deepCopiedKeys1);
    }


    public static boolean isEqualsIgnoringKeyType(Reference reference, Reference reference1) {
        return isEqualsIgnoringKeyType(reference, reference1.getKeys());
    }


    private static Key deepCopyKeyWithoutKeyElement(Key k) {
        return new DefaultKey.Builder()
                .value(k.getValue())
                .idType(k.getIdType())
                .build();
    }


    public static void completeReference(Reference reference, AssetAdministrationShellEnvironment env) {
        List<Key> keys = reference.getKeys();
        if (keys.stream().allMatch(x -> x.getType() != null)) {
            return;
        }
        final Referable[] parent = {
                null
        };
        for (Key k: keys) {
            if (env.getAssetAdministrationShells().stream().anyMatch(x -> x.getIdentification().getIdentifier().equalsIgnoreCase(k.getValue()) ||
                    x.getIdShort().equalsIgnoreCase(k.getValue()))) {
                k.setType(KeyElements.ASSET_ADMINISTRATION_SHELL);
                continue;
            }
            else if (env.getSubmodels().stream().anyMatch(x -> x.getIdentification().getIdentifier().equalsIgnoreCase(k.getValue()) ||
                    x.getIdShort().equalsIgnoreCase(k.getValue()))) {
                k.setType(KeyElements.SUBMODEL);
                continue;
            }
            else if (env.getConceptDescriptions().stream().anyMatch(x -> x.getIdentification().getIdentifier().equalsIgnoreCase(k.getValue()) ||
                    x.getIdShort().equalsIgnoreCase(k.getValue()))) {
                k.setType(KeyElements.CONCEPT_DESCRIPTION);
                continue;
            }
            else if (env.getAssets().stream().anyMatch(x -> x.getIdentification().getIdentifier().equalsIgnoreCase(k.getValue()) ||
                    x.getIdShort().equalsIgnoreCase(k.getValue()))) {
                k.setType(KeyElements.ASSET);
                continue;
            }
            if (parent[0] == null) {
                env.getSubmodels().forEach(x -> x.getSubmodelElements()
                        .forEach(y -> {
                            if (y.getIdShort().equalsIgnoreCase(k.getValue())) {
                                k.setType(AasUtils.referableToKeyType(y));
                                parent[0] = y;
                            }
                        }));
            }
            else if (SubmodelElementCollection.class.isAssignableFrom(parent[0].getClass())) {
                ((SubmodelElementCollection) parent[0]).getValues().forEach(x -> {
                    if (x.getIdShort().equalsIgnoreCase(k.getValue())) {
                        k.setType(AasUtils.referableToKeyType(x));
                        parent[0] = x;
                    }
                });
            }
            else if (Operation.class.isAssignableFrom(parent[0].getClass())) {
                ((Operation) parent[0]).getOutputVariables().forEach(x -> {
                    if (x.getValue().getIdShort().equalsIgnoreCase(k.getValue())) {
                        k.setType(AasUtils.referableToKeyType(x.getValue()));
                        parent[0] = x.getValue();
                    }
                });
                ((Operation) parent[0]).getInoutputVariables().forEach(x -> {
                    if (x.getValue().getIdShort().equalsIgnoreCase(k.getValue())) {
                        k.setType(AasUtils.referableToKeyType(x.getValue()));
                        parent[0] = x.getValue();
                    }
                });
                ((Operation) parent[0]).getInputVariables().forEach(x -> {
                    if (x.getValue().getIdShort().equalsIgnoreCase(k.getValue())) {
                        k.setType(AasUtils.referableToKeyType(x.getValue()));
                        parent[0] = x.getValue();
                    }
                });
            }
        }
    }
}

package org.spongepowered.asm.mixin;

import org.apache.logging.log4j.core.helpers.Booleans;

public abstract class MixinEnvironment {
    /**
     * Mixin options
     */
    public static enum Option {
        
        /**
         * Enable all debugging options
         */
        DEBUG_ALL("debug"),
        
        /**
         * Enable post-mixin class export. This causes all classes to be written
         * to the .mixin.out directory within the runtime directory
         * <em>after</em> mixins are applied, for debugging purposes. 
         */
        DEBUG_EXPORT(Option.DEBUG_ALL, "export"),
        
        /**
         * Export filter, if omitted allows all transformed classes to be
         * exported. If specified, acts as a filter for class names to export
         * and only matching classes will be exported. This is useful when using
         * Fernflower as exporting can be otherwise very slow. The following
         * wildcards are allowed:
         * 
         * <dl>
         *   <dt>*</dt><dd>Matches one or more characters except dot (.)</dd>
         *   <dt>**</dt><dd>Matches any number of characters</dd>
         *   <dt>?</dt><dd>Matches exactly one character</dd>
         * </dl>
         */
        DEBUG_EXPORT_FILTER(Option.DEBUG_EXPORT, "filter", false),
        
        /**
         * Allow fernflower to be disabled even if it is found on the classpath
         */
        DEBUG_EXPORT_DECOMPILE(Option.DEBUG_EXPORT, "decompile") {
            @Override
            boolean getBooleanValue() {
                return Booleans.parseBoolean(System.getProperty(this.property), super.getBooleanValue());
            }
        },
        
        /**
         * Run the CheckClassAdapter on all classes after mixins are applied 
         */
        DEBUG_VERIFY(Option.DEBUG_ALL, "verify"),
        
        /**
         * Enable verbose mixin logging (elevates all DEBUG level messages to
         * INFO level) 
         */
        DEBUG_VERBOSE(Option.DEBUG_ALL, "verbose"),
        
        /**
         * Elevates failed injections to an error condition, see
         * {@link Inject#expect} for details
         */
        DEBUG_INJECTORS(Option.DEBUG_ALL, "countInjections"),
        
        /**
         * Disable the injector handler remapper
         */
        DEBUG_DISABLE_HANDLER_REMAP(Option.DEBUG_ALL, "disableHandlerRename") {
            @Override
            boolean getBooleanValue() {
                return Booleans.parseBoolean(System.getProperty(this.property), super.getBooleanValue());
            }
        },

        /**
         * Dumps the bytecode for the target class to disk when mixin
         * application fails
         */
        DUMP_TARGET_ON_FAILURE("dumpTargetOnFailure"),
        
        /**
         * Enable all checks 
         */
        CHECK_ALL("checks"),
        
        /**
         * Checks that all declared interface methods are implemented on a class
         * after mixin application.
         */
        CHECK_IMPLEMENTS(Option.CHECK_ALL, "interfaces"),
        
        /**
         * Ignore all constraints on mixin annotations, output warnings instead
         */
        IGNORE_CONSTRAINTS("ignoreConstraints"),

        /**
         * Enables the hot-swap agent
         */
        HOT_SWAP("hotSwap"),
        
        /**
         * Parent for environment settings
         */
        ENVIRONMENT("env") {
            @Override
            boolean getBooleanValue() {
                return false;
            }
        },
        
        /**
         * Force refmap obf type when required 
         */
        OBFUSCATION_TYPE(Option.ENVIRONMENT, "obf"),
        
        /**
         * Disable refmap when required 
         */
        DISABLE_REFMAP(Option.ENVIRONMENT, "disableRefMap"),
        
        /**
         * Default compatibility level to operate at
         */
        DEFAULT_COMPATIBILITY_LEVEL(Option.ENVIRONMENT, "compatLevel");

        /**
         * Prefix for mixin options
         */
        private static final String PREFIX = "mixin";
        
        /**
         * Parent option to this option, if non-null then this option is enabled
         * if 
         */
        final Option parent;
        
        /**
         * Java property name
         */
        final String property;
        
        /**
         * Whether this property is boolean or not
         */
        final boolean flag;
        
        /**
         * Number of parents 
         */
        final int depth;

        private Option(String property) {
            this(null, property, true);
        }
        
        private Option(String property, boolean flag) {
            this(null, property, flag);
        }
        
        private Option(Option parent, String property) {
            this(parent, property, true);
        }
        
        private Option(Option parent, String property, boolean flag) {
            this.parent = parent;
            this.property = (parent != null ? parent.property : Option.PREFIX) + "." + property;
            this.flag = flag;
            int depth = 0;
            for (; parent != null; depth++) {
                parent = parent.parent;
            }
            this.depth = depth;
        }
        
        Option getParent() {
            return this.parent;
        }
        
        String getProperty() {
            return this.property;
        }
        
        @Override
        public String toString() {
            return this.flag ? String.valueOf(this.getBooleanValue()) : this.getStringValue();
        }
        
        boolean getBooleanValue() {
            return Booleans.parseBoolean(System.getProperty(this.property), false)
                    || (this.parent != null && this.parent.getBooleanValue());
        }
        
        String getStringValue() {
            return (this.parent == null || this.parent.getBooleanValue()) ? System.getProperty(this.property) : null;
        }

        @SuppressWarnings("unchecked")
        <E extends Enum<E>> E getEnumValue(E defaultValue) {
            String value = System.getProperty(this.property, defaultValue.name());
            try {
                return (E) Enum.valueOf(defaultValue.getClass(), value);
            } catch (IllegalArgumentException ex) {
                return defaultValue;
            }
        }
    }
}

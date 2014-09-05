require 'asciidoctor'
require 'erb'

guard 'shell' do
  watch(%r{^src/docs/.+$}) {|m|
    Asciidoctor.render_file('src/docs/user/ProActiveUserGuide.adoc',  :to_dir => 'build', :safe => :unsafe)
    Asciidoctor.render_file('src/docs/developer/ProActiveDeveloperGuide.adoc',  :to_dir => 'build', :safe => :unsafe)
    Asciidoctor.render_file('src/docs/admin/ProActiveAdminGuide.adoc',  :to_dir => 'build', :safe => :unsafe)
  }
end

guard 'livereload' do
  watch(%r{^build/.+\.(html)})
end